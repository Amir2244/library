package library.dtos;


import jakarta.validation.constraints.*;
import lombok.*;


import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    @PastOrPresent
    private Year publicationYear;

    @NotBlank
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$")
    private String isbn;
}
