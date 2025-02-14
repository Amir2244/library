package library.repositories;

import library.entities.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatronRepository extends JpaRepository<Patron, Long> {
    Optional<Patron> findByEmail(String email);
    boolean existsByEmail(String email);
}