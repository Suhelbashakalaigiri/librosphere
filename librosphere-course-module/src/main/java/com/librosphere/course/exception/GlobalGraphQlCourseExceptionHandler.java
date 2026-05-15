package com.librosphere.course.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GlobalGraphQlCourseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalGraphQlCourseExceptionHandler.class);

    @GraphQlExceptionHandler
    public GraphQLError handleCourseNotFoundException(CourseNotFoundException ex, DataFetchingEnvironment env) {
        log.warn("Course not found: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.NOT_FOUND);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleDuplicateCourseMaterialException(DuplicateCourseMaterialException ex, DataFetchingEnvironment env) {
        log.warn("Duplicate course material: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidBookAssignmentException(InvalidBookAssignmentException ex, DataFetchingEnvironment env) {
        log.warn("Invalid book assignment: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBookVersionMismatchException(BookVersionMismatchException ex, DataFetchingEnvironment env) {
        log.warn("Book version mismatch: {}", ex.getMessage());
        return buildError(ex, env, ErrorType.BAD_REQUEST);
    }

    private GraphQLError buildError(RuntimeException ex, DataFetchingEnvironment env, ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
