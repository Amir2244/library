package library.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class BorrowingRecordResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long patronId;
    private String patronName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}