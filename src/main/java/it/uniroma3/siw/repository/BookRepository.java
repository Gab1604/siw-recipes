
package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Book;
import jakarta.transaction.Transactional;

public interface BookRepository extends CrudRepository<Book, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO author_book VALUES (:bookId, :authorId)", nativeQuery = true)
    public void addAuthorToBook(Long bookId, Long authorId);

    public boolean existsByTitleAndYear(String title, Integer year);

    public List<Book> findByYear(int year);


    @Query(value = "select * "
            + "from book b "
            + "where b.id not in "
            + "(select books_id "
            + "from author_books "
            + "where author_id = :authorId)", nativeQuery = true)
    Iterable<Book> findBookNotInAuthor(@Param("authorId") Long Id);

    @Query("select b from Book b left join fetch b.authors where b.id = :id")
    Book findByIdWithAuthors(@Param("id") Long id);

    List<Book> findByTitleContainingIgnoreCase(String title);

}
