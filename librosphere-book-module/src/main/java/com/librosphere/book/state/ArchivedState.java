package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public final class ArchivedState implements BookState {
    @Override
    public BookStatus getStatus() {
        return BookStatus.ARCHIVED;
    }

    @Override
    public boolean canTransitionTo(BookStatus nextStatus) {
        return nextStatus == BookStatus.DRAFT;
    }
}
