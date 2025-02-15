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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatronService {

    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    @Cacheable(value = "patrons")
    public List<PatronResponse> getAllPatrons() {
        return patronRepository.findAll().stream()
                .map(this::mapToPatronResponse)
                .toList();
    }

    @Cacheable(value = "patrons", key = "#id")
    public PatronResponse getPatronById(Long id) {
        return patronRepository.findById(id)
                .map(this::mapToPatronResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + id));
    }

    @CachePut(value = "patrons", key = "#result.id")
    @CacheEvict(value = "patrons", allEntries = true)
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

    @CachePut(value = "patrons", key = "#id")
    @CacheEvict(value = "patrons", allEntries = true)
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

    @CacheEvict(value = "patrons", allEntries = true)
    public void deletePatron(Long id) {
        Patron patron = patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + id));

        if (borrowingRecordRepository.existsByPatronIdAndReturnDateIsNull(id)) {
            throw new ConflictException("Cannot delete patron with active borrowings");
        }

        patronRepository.delete(patron);
    }

    @Cacheable(value = "patron-history", key = "#patronId")
    public List<BorrowingRecordResponse> getBorrowingHistory(Long patronId) {
        if (!patronRepository.existsById(patronId)) {
            throw new ResourceNotFoundException("Patron not found with id: " + patronId);
        }

        return borrowingRecordRepository.findByPatronIdOrderByBorrowDateDesc(patronId)
                .stream()
                .map(this::mapToBorrowingRecordResponse)
                .toList();
    }

    private PatronResponse mapToPatronResponse(Patron patron) {
        return new PatronResponse(
                patron.getId(),
                patron.getName(),
                patron.getContactInfo(),
                patron.getEmail()
        );
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
