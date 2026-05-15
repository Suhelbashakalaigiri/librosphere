package com.librosphere.lending.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalGraphQlLendingExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalGraphQlLendingExceptionHandler.class);

    @GraphQlExceptionHandler
    public GraphQLError handleBookUnavailableException(BookUnavailableException ex, DataFetchingEnvironment env) {
        logger.warn("Book unavailable: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleIssueLimitExceededException(IssueLimitExceededException ex, DataFetchingEnvironment env) {
        logger.warn("Issue limit exceeded: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleDuplicateIssueRequestException(DuplicateIssueRequestException ex, DataFetchingEnvironment env) {
        logger.warn("Duplicate issue request: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidReturnException(InvalidReturnException ex, DataFetchingEnvironment env) {
        logger.warn("Invalid return: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleRenewalLimitExceededException(RenewalLimitExceededException ex, DataFetchingEnvironment env) {
        logger.warn("Renewal limit exceeded: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleConcurrentIssueException(ConcurrentIssueException ex, DataFetchingEnvironment env) {
        logger.warn("Concurrent issue conflict: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleLendingException(LendingException ex, DataFetchingEnvironment env) {
        logger.warn("Lending exception: {}", ex.getMessage());
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleException(Exception ex, DataFetchingEnvironment env) {
        logger.error("Unexpected error occurred in lending module", ex);
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .build();
    }
}
