package com.librosphere.review.exception;

import com.librosphere.book.exception.BookNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;


@Component
public class GlobalGraphgQlReviewExceptionHanler {

    private static final Logger log = LoggerFactory.getLogger(GlobalGraphgQlReviewExceptionHanler.class);

    @GraphQlExceptionHandler
    public GraphQLError handleBookNotInReviewStateException(
            BookNotInReviewStateException ex,
            DataFetchingEnvironment env) {

        log.warn("Book is not in Review: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleDuplicateReviewException(
             DuplicateReviewException ex,
            DataFetchingEnvironment env) {

        log.warn("Book is not allowed to be in review simulaneously: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidBookStateTransitionException(
            InvalidBookStateTransitionException ex,
            DataFetchingEnvironment env) {

        log.warn("Book Status is Invalid: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidReviewStateException(
            InvalidReviewStateException ex,
            DataFetchingEnvironment env) {

        log.warn("Review state is invalid: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleReviewNotFoundException(
            ReviewNotFoundException ex,
            DataFetchingEnvironment env) {

        log.warn("Review not Found: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
