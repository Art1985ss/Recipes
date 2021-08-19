package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/recipe")
public class RecipeController {
    private final RecipeService service;

    @Autowired
    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping(path = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe.ID> addRecipe(@RequestBody Recipe recipe) {
        try {
            Recipe.ID id = service.save(recipe);
            return ResponseEntity.ok(id);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        try {
            Recipe recipe = service.findById(id);
            return ResponseEntity.ok(recipe);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<Recipe>> search(@RequestParam @NotBlank Optional<String> category,
                                               @RequestParam @NotBlank Optional<String> name) {
        try {
            List<Recipe> recipes = service.search(category, name);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody Recipe recipe) {
        try {
            service.update(id, recipe);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
