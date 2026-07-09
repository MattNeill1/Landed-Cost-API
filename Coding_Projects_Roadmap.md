# Coding Projects Roadmap — Matt Neill

Two portfolio projects designed to close the "raw code" gap on your resume while playing to your inventory / costing / ERP strengths, so you can build them with authority and talk about them convincingly in interviews.

**The strategy:** each project is phased into milestones. Commit at the end of every milestone — a GitHub history that shows steady, incremental progress is itself a signal to hiring managers. Ship a rough working version early, then layer on quality. Don't wait until it's "perfect" to push.

**Two projects, on purpose:**
- **Project 1 (Spring Boot API)** directly answers the exact gap recruiters like Jason flagged — Java 17 + Spring + REST/microservice patterns.
- **Project 2 (React + Node)** gives you a *demoable* full-stack app with a UI you can show live, which is the strongest single portfolio signal for junior-to-mid roles.

Build Project 1 first (it's closer to your existing mental model — backend, data, business logic). Project 2 second, and you can even have its frontend talk to a Node API that mirrors patterns you just learned.

---

## Project 1 — "LandedCost API" (Java 17 + Spring Boot 3)

A REST API for tracking inventory items, purchase receipts, and allocating **landed cost** (freight, duty, insurance) across the items on a shipment. This is *your wheelhouse* — you shipped exactly this kind of engine at Nextworld — so the domain logic will come naturally and you can focus your energy on learning the Java/Spring mechanics.

**Why it's a strong portfolio piece:** it's not a to-do app. It's a real business-domain API with non-trivial allocation logic, which lets you talk about design decisions (how to split a $500 freight charge across items by weight vs. value vs. quantity) — exactly the kind of conversation that separates a memorable candidate from a bootcamp clone.

### Stack
- **Java 21 LTS** (or 17 if a job spec calls for it — syntax is nearly identical; 21 is current)
- **Spring Boot 3.x** — Spring Web (REST), Spring Data JPA, Bean Validation
- **PostgreSQL** (via Docker) for real; **H2** in-memory for tests/quick start
- **Maven** or **Gradle** (Maven is more common in enterprise Java job specs — use it)
- **JUnit 5 + Mockito** for tests
- **Springdoc OpenAPI** for auto-generated Swagger docs

### Data model (start here)
- `Item` — id, sku, description, unitCost, quantityOnHand
- `Shipment` — id, reference, shipDate, freightCost, dutyCost, insuranceCost
- `ShipmentLine` — links a Shipment to an Item, with quantity and pre-allocation unit cost
- `landedUnitCost` — computed: base cost + that line's share of the shipment's freight/duty/insurance

### Milestones

**M1 — Hello Spring (get it running).**
Generate the project at [start.spring.io](https://start.spring.io) with Web, JPA, Validation, PostgreSQL, H2 dependencies. Build one `GET /api/health` endpoint that returns `{"status":"ok"}`. Run it, hit it in the browser. *Learn:* project structure, `@RestController`, `@GetMapping`, how Spring Boot's embedded server works.

**M2 — Items CRUD.**
Full create/read/update/delete for `Item`, backed by H2 first. *Learn:* JPA `@Entity`, `JpaRepository`, `@Service`/`@Repository` layering, DTOs vs. entities, `@RequestBody`/`@PathVariable`, returning proper HTTP status codes.

**M3 — Shipments + the allocation engine.**
Add `Shipment` and `ShipmentLine`. Write a `LandedCostService` that allocates freight/duty/insurance across lines. Start with allocation **by value**; make the method pluggable so you can add **by weight** and **by quantity** later. *Learn:* service-layer business logic, `BigDecimal` for money (never use `double` for currency — good interview talking point), transactional boundaries with `@Transactional`.

**M4 — Validation + error handling.**
Add Bean Validation (`@NotNull`, `@Positive`) and a `@RestControllerAdvice` global exception handler returning clean JSON errors. *Learn:* input validation, centralized error handling, why 400 vs. 404 vs. 422 matter.

**M5 — Real database + tests.**
Swap H2 for PostgreSQL in Docker (`docker-compose.yml`). Write unit tests for the allocation math (this is where bugs hide — test the rounding!) and integration tests for the endpoints with `@SpringBootTest`. *Learn:* profiles (`application-dev.yml` vs `application-test.yml`), Testcontainers or H2 for tests, meaningful test coverage.

**M6 — Polish for the portfolio.**
Add Swagger/OpenAPI docs, a solid README with setup steps and a sample request/response, and a Dockerfile so anyone can run it. *Learn:* API documentation, containerizing a Java app.

### Stretch goals (great "what's next" interview answers)
- Add **Flyway** for database migrations (very enterprise-relevant).
- Add basic **JWT auth** with Spring Security.
- Split into two small services (Items service + Shipments service) to genuinely touch the "microservices" buzzword.

### Resume line (once M6 is done)
> Built a Spring Boot 3 / Java 21 REST API implementing multi-strategy landed-cost allocation over a PostgreSQL data model, with validation, global error handling, JUnit/Mockito test coverage, and OpenAPI docs. *(GitHub link)*

---

## Project 2 — "AppTrack" (React + Node/Express full-stack)

A job-application tracker web app — a clean full-stack CRUD app with a real UI. **You'll actually use it** (you're job hunting right now), which keeps motivation high, and it's directly demoable: you can send a hiring manager a live link.

**Why this one:** the domain is simple on purpose so the *engineering* is the star — you're showing you can build a working frontend, a backend API, wire them together, and deploy. That's the exact skill set junior-to-mid full-stack listings screen for. (If you'd rather flex domain expertise instead, swap the theme to an **inventory dashboard** that reads from your Project 1 API — same architecture, and it links your two projects together nicely.)

### Stack
- **Frontend:** React 18 + **Vite** (fast, modern; avoid Create React App, it's deprecated)
- **Styling:** Tailwind CSS (quick, looks professional)
- **Backend:** Node.js + **Express**
- **Database:** PostgreSQL (or SQLite to start — zero setup)
- **HTTP:** `fetch` or `axios` from the frontend
- **Deploy:** frontend on **Vercel**, backend on **Render** or **Railway** (all have free tiers)

### Milestones

**M1 — Frontend shell.**
`npm create vite@latest`, get a React app rendering a static list of hard-coded applications. Add Tailwind. *Learn:* components, props, JSX, the Vite dev server.

**M2 — Interactive UI with local state.**
Add a form to create an application and render the list from React state. Add status badges (Applied / Interviewing / Rejected / Offer). *Learn:* `useState`, controlled form inputs, list rendering with `.map()` and keys, lifting state up.

**M3 — Node/Express API.**
Separate backend: Express server with REST endpoints (`GET/POST/PUT/DELETE /api/applications`), data in an in-memory array first. *Learn:* Express routing, middleware, `req`/`res`, CORS, REST conventions (mirrors what you learned in Project 1 — notice the parallels).

**M4 — Wire frontend to backend.**
Replace local state with `fetch` calls to your API. Handle loading and error states. *Learn:* `useEffect`, async data fetching, the request/response lifecycle, why CORS exists.

**M5 — Real persistence.**
Swap the in-memory array for a real database (SQLite via `better-sqlite3` to start, or PostgreSQL). *Learn:* SQL from app code (plays to your strength), parameterized queries (and why — SQL injection), basic schema design.

**M6 — Deploy + polish.**
Deploy backend to Render, frontend to Vercel, point one at the other. Write a README with a live link and a screenshot. *Learn:* environment variables, build vs. dev, CORS in production, reading deploy logs.

### Stretch goals
- Add **auth** (email/password or a service like Clerk/Auth0) so it's multi-user.
- Add **search/filter/sort** on the list.
- Add a small **dashboard** (counts by status, a simple chart with Recharts).
- Import your existing tracker data from the spreadsheet.

### Resume line (once M6 is done)
> Built and deployed a full-stack React (Vite) + Node/Express + PostgreSQL job-tracking app with REST API, persistent storage, and a responsive Tailwind UI. Live demo + source on GitHub.

---

## Ground rules that make these count

**Git from commit #1.** Create the GitHub repo before you write code. Commit at every milestone with clear messages. Hiring managers read commit history.

**READMEs are not optional.** For a portfolio project the README *is* the first impression: what it does, how to run it, a screenshot or sample request, and what you'd do next. Many candidates skip this — a good README makes you stand out more than another feature.

**Prefer depth over breadth.** Two finished, deployed, documented projects beat five half-built ones. Finish Project 1 through M6 before starting Project 2 if time is tight.

**Suggested pace (if you've got ~2–3 focused weeks):** Project 1 M1–M3 in week one, M4–M6 in week two; Project 2 in week three. Adjust to reality — done-and-shipped is the only metric that matters.

**When you're ready to build**, I can scaffold either repo for you (folder structure, boilerplate, first working endpoint), pair through any milestone, review your code, or help debug. Just say the word and which project.
