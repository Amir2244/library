package library.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Year;

@Entity
@Getter @Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Column(nullable = false)
    private String author;

    @NotNull(message = "Publication year is mandatory")
    @Column(name = "publication_year", nullable = false)
    private Year publicationYear;

    @NotBlank(message = "ISBN is mandatory")
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$",
            message = "Invalid ISBN format")
    private String isbn;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean available = true;


    public Book() {}
}