
package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {

    public boolean existsByNameAndSurname(String name, String surname);	

	@Query(value="select * "
			+ "from author a "
			+ "where a.id not in "
			+ "(select authors_id "
			+ "from book_authors "
			+ "where book_id = :bookId)", nativeQuery=true)

    Iterable<Author> findAuthorsNotInBook(@Param("bookId")Long Id);
    
	@Query("select a from Author a left join fetch a.books where a.id = :id")
	Author findByIdWithBooks(@Param("id") Long id);

	List<Author> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}
