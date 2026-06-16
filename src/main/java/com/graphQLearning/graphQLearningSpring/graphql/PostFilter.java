package com.graphQLearning.graphQLearningSpring.graphql;

import lombok.Data;

@Data
public class PostFilter {
    private String titleContains;
    private String category;
    private String authorId;
}