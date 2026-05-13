package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public class BookStateFactory {
    public static BookState get(BookStatus status) {
        return switch (status) {
            case DRAFT -> new DraftState();
            case REVIEW -> new ReviewState();
            case PUBLISHED -> new PublishedState();
            case ARCHIVED -> new ArchivedState();
        };
    }
}
