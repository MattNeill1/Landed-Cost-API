# LandedCost API — Progress & Context

Personal learning project: a Spring Boot REST API for inventory + landed-cost allocation. Goal is to build real "hard code" for a junior-to-mid developer portfolio. Full plan is in `Coding_Projects_Roadmap.md` (Project 1).

**Developer:** Matt Neill — new to Mac and to hands-on Java/Spring; strong domain background in inventory/costing/ERP. Prefers to type the code himself and learn each concept; wants clear, beginner-friendly explanations. Be concise and direct.

## Environment
- macOS. Java: Temurin OpenJDK **21.0.11 LTS** (working).
- **Spring Boot 4.1.0**, Maven (via included `./mvnw` wrapper — Maven not separately installed).
- Editor: **VS Code** with "Extension Pack for Java".
- Run the app: `./mvnw spring-boot:run` (first run downloads deps; stop with Ctrl+C).
- DB: H2 in-memory (auto-configured) for now; PostgreSQL driver present for later.
- Package: `com.example.demo`. Main class: `DemoApplication`.

## Git / GitHub
- Local git initialized; `.gitignore` includes standard Spring + `.DS_Store`.
- Remote: **https://github.com/MattNeill1/Landed-Cost-API** (branch `main` tracks `origin/main`).
- Rhythm: after each milestone → `git add .` → `git commit -m "..."` → `git push`.

## Milestone status (roadmap Project 1)
- [x] **M1 — Hello Spring.** `HealthController` serves `GET /api/health` → `{"status":"ok","service":"landed-cost-api"}`. Verified in browser. Committed + pushed.
- [x] **M2 — Items CRUD.** DONE. `Item` JPA entity (id, sku, description, unitCost as BigDecimal, quantityOnHand); `ItemRepository extends JpaRepository<Item, Long>`; `ItemController` with POST/GET(all)/GET(one)/PUT/DELETE at `/api/items`, using constructor injection, `Optional`, and `ResponseEntity` for 404/204 handling. PUT does full-replace (sets id from path). Tested via Postman. Committed + pushed.
- [x] **M3 — Shipments + landed-cost allocation engine.** DONE. `Shipment`/`ShipmentLine` JPA entities (bidirectional OneToMany, `mappedBy="shipment"`, cascade ALL); `LineAllocation` response POJO; `AllocationMethod` enum (VALUE/WEIGHT/QUANTITY). `AllocationService` spreads freight+duty+insurance pool using a running-total technique so pennies sum exactly to the pool; `BigDecimal` throughout with `HALF_UP` rounding. `ShipmentController`: POST create, GET list, GET `/{id}/allocation`. Tested via Postman (create + allocation). Committed + pushed. NOTE: create response is verbose due to bidirectional serialization — `@JsonManagedReference`/`@JsonBackReference` (or DTO) fix deferred.
- [x] **M4 — Validation + global error handling.** DONE. Bean Validation on `Item` (`@NotBlank` sku; `@NotNull`+`@PositiveOrZero` unitCost & quantityOnHand); `@Valid` on `ItemController` POST/PUT `@RequestBody`. `GlobalExceptionHandler` (`@RestControllerAdvice`) handles `MethodArgumentNotValidException` → 400 with `{field: message}` map, `IllegalStateException` (zero allocation basis) → 400 `{error}`, and catch-all `Exception` → 500 with safe generic message (no stack-trace leak). `spring-boot-starter-validation` already in pom. Tested clean 400s. Committed + pushed.
- [~] M5 — PostgreSQL via Docker + JUnit/Mockito tests. **Part A DONE:** `docker-compose.yml` (postgres:16, db `landedcost`, user/pw `landed`, port 5432, named volume for persistence); `application.properties` points at Postgres with `ddl-auto=update` + `show-sql=true`. Verified persistence across restarts. **Part B (JUnit/Mockito tests) — next.**
- [ ] M6 — OpenAPI/Swagger docs, README, Dockerfile.

## Teaching notes
- Matt writes the code; assistant explains each annotation/concept and reviews before running.
- Use VS Code "Source Action → Generate Getters and Setters" for boilerplate.
- Money = `BigDecimal`, never `double`.
