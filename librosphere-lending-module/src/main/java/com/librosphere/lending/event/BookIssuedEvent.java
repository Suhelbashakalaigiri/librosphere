package com.librosphere.lending.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookIssuedEvent extends ApplicationEvent {
    private final Long issueId;
    private final Long bookId;
    private final String userId;

    public BookIssuedEvent(Object source, Long issueId, Long bookId, String userId) {
        super(source);
        this.issueId = issueId;
        this.bookId = bookId;
        this.userId = userId;
    }
}
