package library.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Year;

@Data
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private Year publicationYear;
    private String isbn;
    private boolean available;
}