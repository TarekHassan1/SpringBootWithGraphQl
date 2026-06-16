package com.graphQLearning.graphQLearningSpring.graphql;

import com.graphQLearning.graphQLearningSpring.dto.AuthorInput;
import com.graphQLearning.graphQLearningSpring.dto.PostInput;
import com.graphQLearning.graphQLearningSpring.entity.Author;
import com.graphQLearning.graphQLearningSpring.entity.Post;
import com.graphQLearning.graphQLearningSpring.repository.AuthorRepository;
import com.graphQLearning.graphQLearningSpring.repository.PostRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class MutationController {

    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;

    public MutationController(AuthorRepository authorRepository, PostRepository postRepository) {
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    // ==========================================
    // AUTHOR MUTATIONS
    // ==========================================

    @MutationMapping
    public Author createAuthor(@Argument AuthorInput input) {
        Author author = new Author();
        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());
        return authorRepository.save(author);
    }

    @MutationMapping
    public Author updateAuthor(@Argument Long id, @Argument AuthorInput input) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());
        return authorRepository.save(author);
    }

    @MutationMapping
    public Boolean deleteAuthor(@Argument Long id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ==========================================
    // POST MUTATIONS
    // ==========================================

    @MutationMapping
   // @PreAuthorize("hasRole('ADMIN')")
    public Post createPost(@Argument PostInput input) {
        if (input.getAuthorId() == null) {
            throw new IllegalArgumentException("Author ID input cannot be null");
        }
        Long cleanAuthorId;
        try {
            cleanAuthorId = Long.valueOf(input.getAuthorId().trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid Author ID format received: " + input.getAuthorId());
        }

        // Fetch the author
        Author author = authorRepository.findById(cleanAuthorId)
                .orElseThrow(() -> new RuntimeException("Author not found in database with ID: " + cleanAuthorId));

        Post post = new Post();
        post.setTitle(input.getTitle());
        post.setText(input.getText());
        post.setCategory(input.getCategory());
        post.setAuthor(author);

        return postRepository.save(post);
    }

    @MutationMapping
    public Post updatePost(@Argument Long id, @Argument PostInput input) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Author author = authorRepository.findById(Long.valueOf(input.getAuthorId()))
                .orElseThrow(() -> new RuntimeException("Author not found"));

        post.setTitle(input.getTitle());
        post.setText(input.getText());
        post.setCategory(input.getCategory());
        post.setAuthor(author);
        return postRepository.save(post);
    }

    @MutationMapping
    public Boolean deletePost(@Argument Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
}