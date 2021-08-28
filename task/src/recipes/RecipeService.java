package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.validation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class RecipeService {
    private final RecipeRepository repository;
    private final Validator validator;

    @Autowired
    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public Recipe.ID save(Recipe recipe, User user) throws ValidationException {
        Set<ConstraintViolation<Recipe>> violations = validator.validate(recipe);
        if (!violations.isEmpty()) {
            throw new ValidationException("Non valid recipe to save");
        }
        recipe.setAuthor(user);
        repository.save(recipe);
        return new Recipe.ID(recipe.getId());
    }

    public Recipe findById(long id) throws NoSuchElementException {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No recipe with id " + id + " exists!"));
    }

    public void deleteById(long id, User user) throws NoSuchElementException, AccessDeniedException {
        Recipe recipe = findById(id);
        if (!recipe.getAuthor().equals(user)) {
            throw new AccessDeniedException("User not authorized to delete this recipe");
        }
        repository.delete(recipe);
    }

    public void update(long id, Recipe recipe, User user) throws NoSuchElementException, ValidationException, AccessDeniedException {
        Recipe oldRecipe = findById(id);
        if (!oldRecipe.getAuthor().equals(user)) {
            throw new AccessDeniedException("User not authorized to modify this recipe");
        }
        recipe.setId(oldRecipe.getId());
        save(recipe, user);
    }

    public List<Recipe> search(Optional<String> category, Optional<String> name) throws ValidationException {
        if (category.isPresent() && name.isPresent() || category.isEmpty() && name.isEmpty()) {
            throw new ValidationException("There should be at least one parameter and no more than one.");
        }
        if (category.isPresent()) {
            Set<ConstraintViolation<String>> violations = validator.validate(category.get());
            if (!violations.isEmpty()) {
                throw new ValidationException("Category parameter should not be blank");
            }
            return repository.findAllByCategoryIgnoreCaseOrderByDateDesc(category.get());
        }
        Set<ConstraintViolation<String>> violations = validator.validate(name.get());
        if (!violations.isEmpty()) {
            throw new ValidationException("Name parameter should not be blank");
        }
        return repository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name.get());
    }
}
