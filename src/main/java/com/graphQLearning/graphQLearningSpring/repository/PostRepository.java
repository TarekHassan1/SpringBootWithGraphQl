package com.graphQLearning.graphQLearningSpring.repository;

import com.graphQLearning.graphQLearningSpring.entity.Author;
import com.graphQLearning.graphQLearningSpring.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Post> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Post> findByAuthor(Author author, Pageable pageable);
}