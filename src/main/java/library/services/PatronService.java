package library.services;

import jakarta.transaction.Transactional;
import library.dtos.BorrowingRecordResponse;
import library.dtos.PatronRequest;
import library.dtos.PatronResponse;
import library.entities.BorrowingRecord;
import library.entities.Patron;
import library.exceptions.ConflictException;
import library.exceptions.ResourceNotFoundException;
import library.repositories.BorrowingRecordRepository;
import library.repositories.PatronRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatronService {

    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public List<PatronResponse> getAllPatrons() {
        return patronRepository.findAll().stream()
                .map(this::mapToPatronResponse)
                .toList();
    }

    public PatronResponse getPatronById(Long id) {
        return patronRepository.findById(id)
                .map(this::mapToPatronResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + id));
    }

    @Transactional
    public PatronResponse createPatron(PatronRequest request) {
        if (patronRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        Patron patron = new Patron();
        patron.setName(request.getName());
        patron.setContactInfo(request.getContactInfo());
        patron.setEmail(request.getEmail());

        return mapToPatronResponse(patronRepository.save(patron));
    }

    @Transactional
    public PatronResponse updatePatron(Long id, PatronRequest request) {
        Patron patron = patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + id));

        if (!patron.getEmail().equals(request.getEmail()) &&
                patronRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        patron.setName(request.getName());
        patron.setContactInfo(request.getContactInfo());
        patron.setEmail(request.getEmail());

        return mapToPatronResponse(patronRepository.save(patron));
    }

    @Transactional
    public void deletePatron(Long id) {
        Patron patron = patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + id));

        if (borrowingRecordRepository.existsByPatronIdAndReturnDateIsNull(id)) {
            throw new ConflictException("Cannot delete patron with active borrowings");
        }

        patronRepository.delete(patron);
    }

    private PatronResponse mapToPatronResponse(Patron patron) {
        return new PatronResponse(
                patron.getId(),
                patron.getName(),
                patron.getContactInfo(),
                patron.getEmail()
        );
    }
    public List<BorrowingRecordResponse> getBorrowingHistory(Long patronId) {
        if (!patronRepository.existsById(patronId)) {
            throw new ResourceNotFoundException("Patron not found with id: " + patronId);
        }

        return borrowingRecordRepository.findByPatronIdOrderByBorrowDateDesc(patronId)
                .stream()
                .map(this::mapToBorrowingRecordResponse)
                .toList();
    }

    private BorrowingRecordResponse mapToBorrowingRecordResponse(BorrowingRecord record) {
        return new BorrowingRecordResponse(
                record.getId(),
                record.getBook().getId(),
                record.getBook().getTitle(),
                record.getPatron().getId(),
                record.getPatron().getName(),
                record.getBorrowDate(),
                record.getDueDate(),
                record.getReturnDate()
        );
    }
}