package library.repositories;

import library.entities.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
    Optional<BorrowingRecord> findByBookIdAndPatronIdAndReturnDateIsNull(Long bookId, Long patronId);
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);
    boolean existsByPatronIdAndReturnDateIsNull(Long patronId);
    List<BorrowingRecord> findByPatronIdOrderByBorrowDateDesc(Long patronId);
}
