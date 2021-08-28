package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("${path.recipe}")
public class RecipeController {
    private final RecipeService service;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public RecipeController(RecipeService service, UserDetailsServiceImpl userDetailsService) {
        this.service = service;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe.ID> addRecipe(@RequestBody Recipe recipe,
                                               Principal principal) {
        User user = userDetailsService
                .findByEmail(principal.getName());
        try {
            Recipe.ID id = service.save(recipe, user);
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
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id,
                                             Principal principal) {
        User user = userDetailsService.findByEmail(principal.getName());
        try {
            service.deleteById(id, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@PathVariable long id,
                                       @RequestBody Recipe recipe,
                                       Principal principal) {
        User user = userDetailsService.findByEmail(principal.getName());
        try {
            service.update(id, recipe, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
