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
- [x] **M5 — PostgreSQL via Docker + JUnit/Mockito tests.** DONE. **Part A:** `docker-compose.yml` (postgres:16, db `landedcost`, port 5432, named volume for persistence); `application.properties` points at Postgres with `ddl-auto=update` + `show-sql=true`. Credentials externalized to gitignored `.env` (compose reads it automatically; Spring reads `${DB_USER}`/`${DB_PASSWORD}` — export them in the shell before `./mvnw spring-boot:run`). Added `spring-boot-starter-test` to pom. **Part B:** `AllocationServiceTest` (pure JUnit, no Spring/DB) — VALUE allocation + penny-rounding test proving the running-total split sums to the pool exactly; `ItemControllerTest` (Mockito `@Mock`/`@InjectMocks`, strict stubbing) covers 200/404 on `getItem`. NOTE: `DemoApplicationTests.contextLoads` (`@SpringBootTest`) needs Postgres running + DB env vars exported, else full `./mvnw test` fails; run isolated tests with `-Dtest=...`.
- [x] **M6 — OpenAPI/Swagger docs, README, Dockerfile.** DONE. `springdoc-openapi-starter-webmvc-ui:3.0.3` (v3.x line required for Spring Boot 4) → Swagger UI at `/swagger-ui.html`, spec at `/v3/api-docs`; `@OpenAPIDefinition` on `DemoApplication` sets title/version/description. `README.md` (project pitch, stack, setup, endpoints w/ examples, testing, roadmap) + `.env.example`. Multi-stage `Dockerfile` (JDK build stage → JRE runtime stage, layer-cached deps, `-DskipTests`); `docker-compose.yml` now runs both `app` (build: .) and `db` with a pg healthcheck + `depends_on: service_healthy`; app reaches DB via service name `db` using `SPRING_DATASOURCE_URL` env override. Full stack: `docker compose up --build`. Committed + pushed.

## 2026-07-16 — Backend hardening (while building Project 2 frontend)
Shipment endpoints tightened as the React dashboard started exercising them:
- **`@Valid` on `ShipmentController` POST** — validation annotations added to `Shipment` (`@NotBlank` shipmentNumber; `@NotNull`+`@PositiveOrZero` freightCost; `@PositiveOrZero` duty/insurance) now actually fire (they were dead weight until `@Valid` was added).
- **`DELETE /api/shipments/{id}`** — `existsById` guard → 404, else `deleteById` → 204 No Content.
- **Bidirectional serialization loop RESOLVED** (the M3 deferred item): `@JsonIgnore` on `ShipmentLine.shipment` breaks the infinite Shipment→lines→shipment recursion that was producing malformed/huge JSON once lines actually saved. DB FK + controller's `setShipment` wiring untouched.
- Open follow-ups (optional): `@NotNull` on `allocationMethod`; cascade validation into lines via `@Valid` on the `shipmentLines` field + constraints on `ShipmentLine`.

## Teaching notes
- Matt writes the code; assistant explains each annotation/concept and reviews before running.
- Use VS Code "Source Action → Generate Getters and Setters" for boilerplate.
- Money = `BigDecimal`, never `double`.
