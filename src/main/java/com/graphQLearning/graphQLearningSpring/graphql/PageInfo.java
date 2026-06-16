package com.graphQLearning.graphQLearningSpring.graphql;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageInfo {
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private int totalPages;
    private int totalElements;
    private int currentPage;
}