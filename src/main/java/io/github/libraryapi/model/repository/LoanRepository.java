package io.github.libraryapi.model.repository;

import io.github.libraryapi.model.entity.Book;
import io.github.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository <Loan, Long>{

    @Query(value =
            "SELECT CASE WHEN EXISTS (SELECT 1 FROM Loan l WHERE l.book = :book AND (l.returned IS NULL OR l.returned = false)) THEN true ELSE false END")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query(value =
            "SELECT l FROM Loan AS l JOIN l.book AS b WHERE b.isbn = :isbn OR l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(
            @Param("isbn") String isbn,
            @Param("customer") String customer,
            Pageable pageRequest);

    Page<Loan> findByBook(Book book, Pageable pageable);
}
