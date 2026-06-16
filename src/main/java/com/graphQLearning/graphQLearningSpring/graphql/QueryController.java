package com.graphQLearning.graphQLearningSpring.graphql;


import com.graphQLearning.graphQLearningSpring.entity.Author;
import com.graphQLearning.graphQLearningSpring.entity.Post;
import com.graphQLearning.graphQLearningSpring.repository.AuthorRepository;
import com.graphQLearning.graphQLearningSpring.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class QueryController {

    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;

    public QueryController(AuthorRepository authorRepository, PostRepository postRepository) {
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    // --- Author Queries ---
    @QueryMapping
    public Iterable<Author> allAuthors() {
        return authorRepository.findAll();
    }

    @QueryMapping
    public Optional<Author> author(@Argument Long id) {
        return authorRepository.findById(id);
    }
//    @SchemaMapping(typeName = "Post", field = "author")
//    public Author getAuthor(Post post) {
//        return post.getAuthor();
//    }
    // --- Post Queries ---
    @QueryMapping
    public Iterable<Post> allPosts() {
        return postRepository.findAll();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Post post(@Argument Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    @SchemaMapping(typeName = "Author", field = "posts")
    public List<Post> getPosts(Author author) {
        return author.getPosts();
    }
    @BatchMapping(typeName = "Post", field = "author")
    public Map<Post, Author> getAuthors(List<Post> posts) {
        List<Long> authorIds = posts.stream()
                .map(post -> post.getAuthor().getId())
                .distinct()
                .collect(Collectors.toList());

        List<Author> authors = authorRepository.findAllById(authorIds);

        Map<Long, Author> authorMap = authors.stream()
                .collect(Collectors.toMap(Author::getId, Function.identity()));

        return posts.stream()
                .collect(Collectors.toMap(post -> post, post -> authorMap.get(post.getAuthor().getId())));
    }
    @QueryMapping
    public PostConnection posts(@Argument Integer offset, @Argument Integer limit, @Argument PostFilter filter) {
        // Calculate the page index from the offset (e.g., offset 0 / limit 2 = page 0)
        int pageIndex = offset / limit;
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("id"));
        Page<Post> postPage;

        if (filter != null) {
            if (filter.getTitleContains() != null) {
                postPage = postRepository.findByTitleContainingIgnoreCase(filter.getTitleContains(), pageable);
            } else if (filter.getCategory() != null) {
                postPage = postRepository.findByCategoryIgnoreCase(filter.getCategory(), pageable);
            } else if (filter.getAuthorId() != null) {
                Author author = authorRepository.findById(Long.valueOf(filter.getAuthorId())).orElse(null);
                if (author != null) {
                    postPage = postRepository.findByAuthor(author, pageable);
                } else {
                    postPage = Page.empty(pageable);
                }
            } else {
                postPage = postRepository.findAll(pageable);
            }
        } else {
            postPage = postRepository.findAll(pageable);

        }

        // Bundle elements into our customized GraphQL Connection response block
        return new PostConnection(
                postPage.getContent(),
                new PageInfo(
                        postPage.hasNext(),
                        postPage.hasPrevious(),
                        postPage.getTotalPages(),
                        (int) postPage.getTotalElements(),
                        postPage.getNumber()
                )
        );
    }
}