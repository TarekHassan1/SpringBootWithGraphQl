Graph in Spring

A Spring Boot GraphQL API I built to learn and demonstrate real-world backend patterns — from secure JWT authentication to solving the N+1 query problem.


What I Built

GraphQL API with Full CRUD

Designed and implemented a GraphQL schema with Query and Mutation types for two entities — Author and Post — using Spring for GraphQL's annotation model (@QueryMapping, @MutationMapping).

Offset Pagination + Filtering

Built a custom PostConnection response type from scratch that wraps results with a PageInfo block (total pages, current page, has next/previous). The posts query accepts an optional PostFilter to search by title keyword, category, or author ID — all wired to Spring Data JPA's Pageable.

Solved the N+1 Problem with Batch Loading

Noticed that resolving Post.author naively would fire one SQL query per post. Fixed this by implementing @BatchMapping, which collects all author IDs across the list and resolves them in a single query.

JWT Authentication (RS256)

Wrote a JwtKeyGenerator utility to generate an RSA key pair, then wired up the full token flow:


POST /token accepts HTTP Basic credentials and returns a signed JWT with the user's roles in the scope claim
All subsequent requests are verified stateless via Spring's OAuth2 Resource Server


Role-Based Authorization

Used @EnableMethodSecurity and @PreAuthorize to lock down individual GraphQL operations — e.g. querying a single post requires authentication, and creating posts can be restricted to ADMIN only.

Custom GraphQL Error Handling

Built a GraphQLExceptionResolver that intercepts domain exceptions and maps them to typed GraphQL errors with custom extensions (postId, errorCode), so clients get structured error responses instead of raw server crashes.


Tech Used

Spring Boot 3 · Spring for GraphQL · Spring Security · OAuth2 Resource Server · RS256 JWT (Nimbus JOSE) · Spring Data JPA · H2 · Lombok
