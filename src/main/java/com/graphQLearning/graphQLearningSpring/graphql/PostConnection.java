package com.graphQLearning.graphQLearningSpring.graphql;

import com.graphQLearning.graphQLearningSpring.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PostConnection {
    private List<Post> nodes;
    private PageInfo pageInfo;
}