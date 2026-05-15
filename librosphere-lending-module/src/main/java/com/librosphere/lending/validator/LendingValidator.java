package com.librosphere.lending.validator;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.lending.exception.BookUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class LendingValidator {
    public void validateBookForIssue(BookDto book) {
        if (book.status() != BookStatus.PUBLISHED) {
            throw new BookUnavailableException("Only PUBLISHED books can be issued. Current status: " + book.status());
        }
    }
}
