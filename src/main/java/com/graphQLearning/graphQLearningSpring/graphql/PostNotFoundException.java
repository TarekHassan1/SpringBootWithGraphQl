package com.graphQLearning.graphQLearningSpring.graphql;


import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    private final Long postId;

    public PostNotFoundException(Long postId) {
        super("Post with ID " + postId + " not found.");
        this.postId = postId;
    }

}