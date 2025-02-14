package library.controllers;

import jakarta.validation.Valid;
import library.dtos.BorrowingRecordResponse;
import library.dtos.PatronRequest;
import library.dtos.PatronResponse;
import library.services.PatronService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patrons")
@RequiredArgsConstructor
public class PatronController {

    private final PatronService patronService;

    @GetMapping
    public ResponseEntity<List<PatronResponse>> getAllPatrons() {
        return ResponseEntity.ok(patronService.getAllPatrons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatronResponse> getPatronById(@PathVariable Long id) {
        return ResponseEntity.ok(patronService.getPatronById(id));
    }

    @PostMapping
    public ResponseEntity<PatronResponse> createPatron(
            @Valid @RequestBody PatronRequest request
    ) {
        PatronResponse response = patronService.createPatron(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatronResponse> updatePatron(
            @PathVariable Long id,
            @Valid @RequestBody PatronRequest request
    ) {
        return ResponseEntity.ok(patronService.updatePatron(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatron(@PathVariable Long id) {
        patronService.deletePatron(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/borrowings")
    public ResponseEntity<List<BorrowingRecordResponse>> getBorrowingHistory(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(patronService.getBorrowingHistory(id));
    }
}