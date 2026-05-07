# Spring CRUD Template

A production-ready Spring Boot template providing a **generic, type-safe CRUD architecture** with MapStruct mapping, full audit trails, RFC 7807-compliant error handling, and profile-based configuration.

---

## 🧩 What's Included

| Category | Feature |
|---|---|
| **Generic CRUD** | `BaseController`, `BaseService`, `BaseRepository`, `BaseMapper` — one extension per feature |
| **Type Safety** | `Identifiable<ID>` interface eliminates reflection-based ID assignment |
| **Mapping** | MapStruct 1.6.3 — compile-time type-safe DTO ↔ Entity conversion |
| **Validation** | Jakarta Bean Validation (`@Valid`) on POST/PUT; intentionally skipped on PATCH |
| **Auditing** | `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy` via Spring Data JPA |
| **Optimistic Locking** | `@Version` on `BaseEntity` prevents concurrent lost updates |
| **Pagination** | `PaginatedResponse<T>` record wrapping Spring `Page<T>` |
| **Error Handling** | `GlobalExceptionHandler` covering 400, 404, 405, 409 (constraint + locking), 500 |
| **API Docs** | SpringDoc / Swagger UI with per-endpoint `@Operation` and `@ApiResponses` |
| **CORS** | `CorsConfig` — origins externalized via `cors.allowed-origin-patterns` property |
| **Actuator** | `/actuator/health`, `/actuator/info` — restricted in prod, full exposure in dev |
| **Profiles** | `dev` (H2, show-sql, full actuator) / `prod` (validate DDL, health-only actuator) |
| **Database** | H2 by default; PostgreSQL config commented and ready to uncomment |

---

## 🗂️ Project Structure

```
src/main/java/com/newbie/gym/
│
├── common/
│   ├── base/
│   │   ├── controller/     BaseController<T, RQ, RS, ID>
│   │   ├── entity/         BaseEntity, Identifiable<ID>
│   │   ├── mapper/         BaseMapper<E, RQ, RS>
│   │   ├── repository/     BaseRepository<T, ID>
│   │   └── service/        BaseService<T, ID>
│   │
│   ├── config/
│   │   ├── CorsConfig.java
│   │   └── JpaConfig.java  (AuditorAware)
│   │
│   ├── dto/
│   │   ├── PaginatedResponse.java
│   │   └── Response.java   (RFC 7807 wrapper)
│   │
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   │
│   └── util/
│       └── BeanUtil.java   (null-safe property copy for programmatic PATCH)
│
└── example/                ← Reference implementation — copy to create new features
    ├── controller/         ExampleController.java
    ├── dto/                ExampleRequest.java, ExampleResponse.java
    ├── entity/             Example.java
    ├── mapper/             ExampleMapper.java
    ├── repository/         ExampleRepository.java
    └── service/            ExampleService.java
```

---

## ⚡ Creating a New Feature

Copy the `example` module and replace `Example` with your entity name.

### 1. Entity

```java
@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends BaseEntity {
    @NotBlank
    private String name;
    private Double price;
}
```

### 2. Request DTO

```java
public record ProductRequest(
    @NotBlank(message = "Name is required") String name,
    Double price
) {}
```

### 3. Response DTO

```java
public record ProductResponse(
    Long id,
    String name,
    Double price,
    String createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

### 4. Mapper

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper extends BaseMapper<Product, ProductRequest, ProductResponse> {

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product entity);
}
```

### 5. Repository

```java
public interface ProductRepository extends BaseRepository<Product, Long> {}
```

### 6. Service

```java
@Service
public class ProductService extends BaseService<Product, Long> {
    public ProductService(ProductRepository repository) {
        super(repository);
    }
}
```

### 7. Controller

```java
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController extends BaseController<Product, ProductRequest, ProductResponse, Long> {
    public ProductController(ProductService service, ProductMapper mapper) {
        super(service, mapper);
    }
}
```

That's it. You get `GET /api/products`, `GET /api/products/{id}`, `POST`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` — all with Swagger docs, validation, pagination, and audit fields.

---

## 🔗 API Endpoints (auto-generated per feature)

| Method | Path | Description | Validated |
|---|---|---|---|
| `GET` | `/api/{resource}?page=0&size=10` | Paginated list | — |
| `GET` | `/api/{resource}/{id}` | Single record | — |
| `POST` | `/api/{resource}` | Create | ✅ `@Valid` |
| `PUT` | `/api/{resource}/{id}` | Full replace | ✅ `@Valid` |
| `PATCH` | `/api/{resource}/{id}` | Partial update | ❌ intentional |
| `DELETE` | `/api/{resource}/{id}` | Delete | — |

---

## 📋 Standard Response Format

All endpoints return the same envelope (RFC 7807 inspired):

```json
{
  "timestamp": "2026-05-07T19:50:55",
  "status": 200,
  "message": "Created successfully",
  "data": { ... }
}
```

Validation errors include a field-level map:

```json
{
  "timestamp": "2026-05-07T19:50:55",
  "status": 400,
  "message": "Validation Failed",
  "type": "about:blank",
  "title": "Bad Request",
  "errors": {
    "name": "must not be blank",
    "price": "must be positive"
  }
}
```

---

## 🛡️ Exception Handling

| Exception | HTTP Status | When |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entity not found by ID |
| `NoResourceFoundException` | 404 | Route does not exist |
| `MethodArgumentNotValidException` | 400 | `@Valid` constraint failure |
| `HttpMessageNotReadableException` | 400 | Malformed JSON body |
| `HttpRequestMethodNotSupportedException` | 405 | Wrong HTTP method |
| `DataIntegrityViolationException` | 409 | Unique/FK constraint violation |
| `ObjectOptimisticLockingFailureException` | 409 | Concurrent update conflict |
| `Exception` | 500 | Unexpected server error |

---

## 📑 BaseEntity Fields

Every entity extending `BaseEntity` automatically gets:

| Field | Type | Managed by |
|---|---|---|
| `id` | `Long` | JPA (`@GeneratedValue`) |
| `version` | `Long` | JPA (`@Version`) — optimistic locking |
| `createdBy` | `String` | Spring Data (`@CreatedBy`) |
| `updatedBy` | `String` | Spring Data (`@LastModifiedBy`) |
| `createdAt` | `LocalDateTime` | Spring Data (`@CreatedDate`) |
| `updatedAt` | `LocalDateTime` | Spring Data (`@LastModifiedDate`) |

All fields are **READ_ONLY** from the API perspective and are never accepted from client input.

---

## 🔒 Audit User Integration (Spring Security)

By default, `createdBy` / `updatedBy` are set to `"system"`. To wire the current authenticated user, replace the `AuditorAware` bean in `JpaConfig.java`:

```java
@Bean
public AuditorAware<String> auditorAware() {
    return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
}
```

---

## ⚙️ Lifecycle Hooks

Override in your concrete service to inject custom logic:

```java
@Service
public class ProductService extends BaseService<Product, Long> {

    @Override
    protected void beforeSave(Product entity) {
        entity.setName(entity.getName().trim());
    }

    @Override
    protected void afterDelete(Long id) {
        log.info("Product {} was deleted", id);
    }
}
```

Available hooks: `beforeSave`, `afterSave`, `beforeUpdate`, `afterUpdate`, `beforeDelete`, `afterDelete`.

---

## 🗄️ Database Configuration

### H2 (default — dev only)

```properties
spring.datasource.url=jdbc:h2:file:./data/app_db
```

H2 console available at `http://localhost:8080/h2-console` when `dev` profile is active.

### PostgreSQL

Uncomment in `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/app_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:your_password}
spring.datasource.driverClassName=${DB_DRIVER:org.postgresql.Driver}
spring.jpa.database-platform=${DB_DIALECT:org.hibernate.dialect.PostgreSQLDialect}
```

---

## 🌐 CORS Configuration

Controlled via property — no code changes needed per environment:

```properties
# Dev
cors.allowed-origin-patterns=http://localhost:*,http://127.0.0.1:*

# Prod
cors.allowed-origin-patterns=https://yourdomain.com
```

---

## 📦 Key Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Spring Boot | 3.4.1 | Framework |
| Spring Data JPA | (managed) | ORM + Auditing |
| Spring Validation | (managed) | Bean Validation |
| SpringDoc OpenAPI | (managed) | Swagger UI |
| MapStruct | 1.6.3 | Compile-time DTO mapping |
| Lombok | (managed) | Boilerplate reduction |
| H2 | (managed) | Embedded dev database |
| Spring Actuator | (managed) | Health checks + metrics |

---

## 🚀 Running Locally

```bash
./mvnw spring-boot:run
```

| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | API Explorer |
| `http://localhost:8080/h2-console` | H2 Database Console (dev only) |
| `http://localhost:8080/actuator/health` | Health check |

---

## 🏭 Production Deployment

1. Set `spring.profiles.active=prod` via environment variable
2. Set database environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`, `DB_DIALECT`
3. Set `cors.allowed-origin-patterns` to your production domain
4. Wire `AuditorAware` to your authentication provider (see section above)
