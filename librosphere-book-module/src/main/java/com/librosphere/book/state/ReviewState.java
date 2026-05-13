package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public final class ReviewState implements BookState {
    @Override
    public BookStatus getStatus() {
        return BookStatus.REVIEW;
    }

    @Override
    public boolean canTransitionTo(BookStatus nextStatus) {
        return nextStatus == BookStatus.PUBLISHED || nextStatus == BookStatus.DRAFT || nextStatus == BookStatus.ARCHIVED;
    }
}
