package com.graphQLearning.graphQLearningSpring.repository;

import com.graphQLearning.graphQLearningSpring.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}