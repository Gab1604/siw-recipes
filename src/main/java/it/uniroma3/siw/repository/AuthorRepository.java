package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
	// Query method di esempio (se ti servisse in futuro):
	// List<Author> findByLastNameContainingIgnoreCase(String lastNamePart);
}
