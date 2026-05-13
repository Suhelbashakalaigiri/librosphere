package com.librosphere.book.exception;

public class DuplicateISBNException extends RuntimeException {
    public DuplicateISBNException(String isbn) {
        super("Book already exists with ISBN: " + isbn);
    }
}
