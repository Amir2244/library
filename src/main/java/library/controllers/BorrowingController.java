package library.controllers;

import library.services.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @PostMapping("/api/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity<Void> borrowBook(
            @PathVariable Long bookId,
            @PathVariable Long patronId
    ) {
        borrowingService.borrowBook(bookId, patronId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/return/{bookId}/patron/{patronId}")
    public ResponseEntity<Void> returnBook(
            @PathVariable Long bookId,
            @PathVariable Long patronId
    ) {
        borrowingService.returnBook(bookId, patronId);
        return ResponseEntity.ok().build();
    }
}