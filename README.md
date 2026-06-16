# 🔷 GraphQLearning Spring

A production-pattern **Spring Boot + GraphQL** backend — built to go beyond tutorials and implement the real problems: N+1 queries, stateless JWT auth, role-based guards, and structured error contracts.

---

## ✦ What I Implemented

### 1. GraphQL API — Queries & Mutations
Full schema with `Query` and `Mutation` types for two related entities (`Author`, `Post`). Built using Spring for GraphQL's annotation model — `@QueryMapping` and `@MutationMapping` — with proper separation into `QueryController` and `MutationController`.

Every entity supports full CRUD:
- `allAuthors`, `author(id)`, `allPosts`, `post(id)`, `posts(...)`
- `createAuthor`, `updateAuthor`, `deleteAuthor`
- `createPost`, `updatePost`, `deletePost`

---

### 2. Offset Pagination + Dynamic Filtering
The `posts` query supports paginated results via custom `offset` and `limit` arguments, returning a `PostConnection` wrapper I designed with:
- `nodes` — the current page of results
- `pageInfo` — `hasNextPage`, `hasPreviousPage`, `totalPages`, `totalElements`, `currentPage`

An optional `PostFilter` input object allows narrowing results by:
- `titleContains` — case-insensitive keyword search
- `category` — exact category match (case-insensitive)
- `authorId` — all posts by a specific author

All filter branches map to dedicated Spring Data JPA repository methods with `Pageable`.

---

### 3. Solving the N+1 Problem with `@BatchMapping`
Naively resolving `Post.author` fires one SQL query per post in the list. I identified and fixed this using Spring for GraphQL's `@BatchMapping`:

Instead of N queries, the resolver collects all post author IDs in one pass, fetches the matching `Author` records in a single query, builds an ID → Author map, and returns the full `Map<Post, Author>` to the framework.

---

### 4. JWT Authentication (RS256 — Asymmetric Keys)
Wrote a `JwtKeyGenerator` utility that generates a 2048-bit RSA key pair and writes both keys to `.pem` files. This means:
- Only this service can **sign** tokens (private key)
- Any downstream service could **verify** tokens using just the public key — no shared secrets

**Token flow:**
1. Client sends HTTP Basic credentials to `POST /token`
2. `AuthController` validates credentials, builds a `JwtClaimsSet` with the user's roles in the `scope` claim, signs it using `JwtEncoder`, and returns the token
3. All subsequent requests include the token as `Authorization: Bearer <token>`
4. Spring's OAuth2 Resource Server validates the signature on every request — completely stateless

---

### 5. Role-Based Authorization with `@PreAuthorize`
Enabled method-level security via `@EnableMethodSecurity` and applied guards directly on GraphQL resolver methods:

- `post(id)` — requires `isAuthenticated()`
- `createPost` — can be restricted to `hasRole('ADMIN')`

A custom `JwtAuthenticationConverter` translates JWT `scope` claims into Spring `ROLE_` authorities so `@PreAuthorize` expressions work cleanly with the token payload.

---

### 6. Structured GraphQL Error Handling
Built a `GraphQLExceptionResolver` extending `DataFetcherExceptionResolverAdapter` that intercepts domain exceptions and returns typed, client-friendly GraphQL errors instead of raw server crashes.

| Exception | GraphQL Error Type | Extensions |
|---|---|---|
| `PostNotFoundException` | `NOT_FOUND` | `postId`, `errorCode: "POST_NOT_FOUND"` |
| Anything else | `INTERNAL_ERROR` | Generic message, no stack trace leaked |

`PostNotFoundException` is a custom runtime exception that carries the `postId` so error responses give clients exactly what they need to handle the failure.

---

### 7. Security Configuration
Configured a stateless `SecurityFilterChain` with:
- CSRF disabled (stateless JWT API)
- `/graphql`, `/graphiql`, `/h2-console` open for development
- `/token` explicitly protected (requires HTTP Basic to exchange for a JWT)
- OAuth2 Resource Server for JWT validation on all other endpoints
- Frame options disabled for H2 console access

---

## Stack

```
Spring Boot 3          Spring for GraphQL     Spring Security
OAuth2 Resource Server  Nimbus JOSE (RS256)   Spring Data JPA
H2 Database            Lombok
```

---

## Quick Start

**1. Generate RSA keys** — run `JwtKeyGenerator.main()` once:
```
src/main/resources/certs/app-private.pem
src/main/resources/certs/app-public.pem
```

**2. `application.properties`**
```properties
rsa.public-key=classpath:certs/app-public.pem
rsa.private-key=classpath:certs/app-private.pem
spring.h2.console.enabled=true
spring.graphql.graphiql.enabled=true
```

**3. Run**
```bash
./mvnw spring-boot:run
```

**4. Get a token**
```bash
curl -X POST http://localhost:8080/token -u tarek:pass123
```

**5. Query** (use token as Bearer in Authorization header)
```graphql
query {
  posts(offset: 0, limit: 5, filter: { category: "Tech" }) {
    nodes { id title author { firstName lastName } }
    pageInfo { hasNextPage totalElements currentPage }
  }
}
```

---

## Test Users

| Username | Password | Role |
|---|---|---|
| `tarek` | `pass123` | USER |
| `admin` | `admin123` | ADMIN |
