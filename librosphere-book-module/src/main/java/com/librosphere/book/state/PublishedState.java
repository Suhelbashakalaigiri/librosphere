package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public final class PublishedState implements BookState {
    @Override
    public BookStatus getStatus() {
        return BookStatus.PUBLISHED;
    }

    @Override
    public boolean canTransitionTo(BookStatus nextStatus) {

        return nextStatus == BookStatus.ARCHIVED;
    }
}
