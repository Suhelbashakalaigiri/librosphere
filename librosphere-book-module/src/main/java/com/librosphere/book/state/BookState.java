package com.librosphere.book.state;

import com.librosphere.book.enums.BookStatus;

public sealed interface BookState 
    permits DraftState, ReviewState, PublishedState, ArchivedState {
    
    BookStatus getStatus();
    
    boolean canTransitionTo(BookStatus nextStatus);
    
    default void validateTransition(BookStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            throw new IllegalStateException("Invalid state transition from " + getStatus() + " to " + nextStatus);
        }
    }
}
