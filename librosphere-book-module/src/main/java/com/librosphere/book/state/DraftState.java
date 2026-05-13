package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public final class DraftState implements BookState {
    @Override
    public BookStatus getStatus() {
        return BookStatus.DRAFT;
    }

    @Override
    public boolean canTransitionTo(BookStatus nextStatus) {
        return nextStatus == BookStatus.REVIEW || nextStatus == BookStatus.ARCHIVED;
    }
}
