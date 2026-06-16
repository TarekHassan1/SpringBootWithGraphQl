package com.graphQLearning.graphQLearningSpring.graphql;


import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // 1. Intercept our custom PostNotFoundException
        if (ex instanceof PostNotFoundException customEx) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.NOT_FOUND) // Standard modern ErrorType enum
                    .message(customEx.getMessage())
                    .extensions(Map.of(
                            "postId", customEx.getPostId(),
                            "errorCode", "POST_NOT_FOUND"
                    ))
                    .build();
        }

        // 2. Fallback protection: Catch any other unexpected server crashes
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected internal server error occurred.")
                .build();
    }
}