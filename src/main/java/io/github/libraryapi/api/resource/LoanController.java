package io.github.libraryapi.api.resource;

import io.github.libraryapi.api.dto.BookDTO;
import io.github.libraryapi.api.dto.LoanDTO;
import io.github.libraryapi.api.dto.LoanFilterDTO;
import io.github.libraryapi.api.dto.ReturnedLoanDTO;
import io.github.libraryapi.model.entity.Book;
import io.github.libraryapi.model.entity.Loan;
import io.github.libraryapi.service.BookService;
import io.github.libraryapi.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private LoanService service;
    private BookService bookService;
    private ModelMapper modelMapper;

    @Autowired
    public LoanController(LoanService service, BookService bookService, ModelMapper modelMapper) {
        this.service = service;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
        Book book = bookService.getBookByIsbn(
                dto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);

        return entity.getId();
    }
    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        service.update(loan);

    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageable){
        Page<Loan> result = service.find(dto, pageable);
        List<LoanDTO> loans = result
                .getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(entity, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(loans, pageable, result.getTotalElements());
    }
}
