package com.librosphere.book.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalGraphQlExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalGraphQlExceptionHandler.class);

    @GraphQlExceptionHandler
    public GraphQLError handleBookNotFoundException(
            BookNotFoundException ex,
            DataFetchingEnvironment env) {

        logger.warn("Book not found: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.NOT_FOUND)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleDuplicateISBNException(
            DuplicateISBNException ex,
            DataFetchingEnvironment env) {

        logger.warn("Duplicate ISBN: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidBookStateException(
            InvalidBookStateException ex,
            DataFetchingEnvironment env) {

        logger.warn("Invalid book state: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleVersioningRequiredException(
            VersioningRequiredException ex,
            DataFetchingEnvironment env) {

        logger.warn("Versioning required: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleException(
            Exception ex,
            DataFetchingEnvironment env) {

        logger.error("Unexpected error occurred", ex);

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .build();
    }
}