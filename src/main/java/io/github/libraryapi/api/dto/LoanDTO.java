package io.github.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
    private Long id;
    private BookDTO book;

    private String isbn;

    private String customer;
}
