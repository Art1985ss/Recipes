package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipe {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @ElementCollection
    @Size(min = 1)
    private List<String> ingredients = new ArrayList<>();
    @ElementCollection
    @Size(min = 1)
    private List<String> directions = new ArrayList<>();
    @NotBlank
    private String category;
    @UpdateTimestamp
    private LocalDateTime date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User author;

    @Data
    @AllArgsConstructor
    public static class ID {
        private long id;
    }
}
