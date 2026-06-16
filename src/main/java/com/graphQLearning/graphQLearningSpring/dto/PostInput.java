package com.graphQLearning.graphQLearningSpring.dto;

import lombok.Data;

@Data
public class PostInput {
    private String title;
    private String text;
    private String category;
    private String authorId;
}