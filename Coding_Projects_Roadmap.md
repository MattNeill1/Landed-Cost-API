# Coding Projects Roadmap — Matt Neill

A sequence of portfolio projects designed to close the "raw code" gap on your resume while playing to your inventory / costing / ERP strengths, so you can build them with authority and talk about them convincingly in interviews.

**The strategy:** each project is phased into milestones. Commit at the end of every milestone — a GitHub history that shows steady, incremental progress is itself a signal to hiring managers. Ship a rough working version early, then layer on quality. Don't wait until it's "perfect" to push.

**Three projects, in sequence:**
- **Project 1 (Spring Boot API)** directly answers the exact gap recruiters like Jason flagged — Java 21 + Spring + REST/microservice patterns. *(Done — M1–M6 complete.)*
- **Project 2 (React frontend over your API)** turns Project 1 into a *demoable* full-stack app: a visible UI sitting on top of the API you already built. It's the strongest single portfolio signal for junior-to-mid roles, and it ties your work together — the same allocation engine you wrote becomes something a person can see and click.
- **Project 3 (React + Node full-stack)** is a *from-scratch* full-stack app (a job tracker you'll actually use), proving you can build a backend yourself too — in a second stack — not just a frontend over an existing one.

Build them in order; each one builds on the last. Project 1 gives you the backend and a clean API. Project 2 puts a face on it and teaches you the frontend with the backend already handled. Project 3 has you build both halves independently.

---

## Project 1 — "LandedCost API" (Java 21 + Spring Boot) — ✅ DONE

A REST API for tracking inventory items, purchase receipts, and allocating **landed cost** (freight, duty, insurance) across the items on a shipment. This is *your wheelhouse* — you shipped exactly this kind of engine at Nextworld — so the domain logic came naturally and you could focus your energy on learning the Java/Spring mechanics.

**Why it's a strong portfolio piece:** it's not a to-do app. It's a real business-domain API with non-trivial allocation logic, which lets you talk about design decisions (how to split a $500 freight charge across items by weight vs. value vs. quantity) — exactly the kind of conversation that separates a memorable candidate from a bootcamp clone.

### Stack
- **Java 21 LTS** (or 17 if a job spec calls for it — syntax is nearly identical; 21 is current)
- **Spring Boot** — Spring Web MVC (REST), Spring Data JPA, Bean Validation
- **PostgreSQL** (via Docker) for real; **H2** in-memory for tests/quick start
- **Maven** (more common in enterprise Java job specs)
- **JUnit 5 + Mockito** for tests
- **Springdoc OpenAPI** for auto-generated Swagger docs

### Data model
- `Item` — id, sku, description, unitCost, quantityOnHand
- `Shipment` — id, shipmentNumber, freightCost, dutyCost, insuranceCost, allocationMethod
- `ShipmentLine` — links a Shipment to an Item, with quantity and weight
- `landedUnitCost` — computed: base cost + that line's share of the shipment's freight/duty/insurance

### Milestones — all complete
- **M1 — Hello Spring.** `GET /api/health`. Project structure, `@RestController`, embedded server.
- **M2 — Items CRUD.** JPA `@Entity`, `JpaRepository`, constructor injection, `ResponseEntity` status codes.
- **M3 — Shipments + allocation engine.** `AllocationService` splits freight/duty/insurance by VALUE/WEIGHT/QUANTITY using a running-total technique so pennies sum exactly. `BigDecimal` throughout.
- **M4 — Validation + error handling.** Bean Validation + `@RestControllerAdvice` global handler returning clean JSON errors.
- **M5 — Real database + tests.** PostgreSQL in Docker; JUnit tests of the allocation math (including the rounding) + a Mockito controller test.
- **M6 — Polish.** Swagger/OpenAPI docs, README, and a multi-stage Dockerfile + full-stack `docker compose up`.

### Stretch goals (great "what's next" interview answers)
- Add **Flyway** for database migrations (very enterprise-relevant).
- Add basic **JWT auth** with Spring Security.
- Fix the bidirectional-JSON verbosity by returning **DTOs** instead of entities (good lead-in to learning DTOs).
- Split into two small services (Items + Shipments) to genuinely touch the "microservices" buzzword.

### Resume line
> Built a Spring Boot / Java 21 REST API implementing multi-strategy landed-cost allocation over a PostgreSQL data model, with validation, global error handling, JUnit/Mockito test coverage, OpenAPI docs, and full Docker containerization. *(GitHub link)*

---

## Project 2 — "LandedCost Dashboard" (React + Vite frontend over your Project 1 API)

A web UI that sits directly on top of the LandedCost API you just built. **No new backend** — this is the frontend half of a full-stack app, talking to the exact endpoints you already shipped. This is the "pull it all together" project: it turns your JSON API into something a person can see, click, and demo live.

**Why it's a strong portfolio piece:** it proves two things at once — that you can build a real frontend, *and* that you understand how the two halves of a full-stack app connect (the single most-screened skill for junior-to-mid full-stack roles). It also showcases your Project 1 work visually: the allocation engine's output becomes a clean results table instead of raw JSON, and reviewers get a live link instead of a Postman screenshot.

### Stack
- **Frontend:** React 18 + **Vite** (fast, modern; avoid Create React App, it's deprecated)
- **Styling:** Tailwind CSS
- **HTTP:** `fetch` or `axios` from the frontend
- **Backend:** your existing Spring Boot LandedCost API — *unchanged*, except enabling CORS
- **Deploy:** frontend on **Vercel**; the API container (you already have a Dockerfile) on **Render** or **Railway**

### The one backend change: CORS
Browsers block a page served from `localhost:5173` (Vite's dev server) from calling an API on `localhost:8080` — different origins. You'll add a small CORS configuration to the Spring app (a `WebMvcConfigurer` bean, or `@CrossOrigin`) to allow your frontend's origin. That's the *only* change the backend needs — everything else you built stays exactly as is.

### Milestones

**M1 — Frontend shell.**
`npm create vite@latest`, get a React app rendering a static, hard-coded Items table styled with Tailwind. *Learn:* components, props, JSX, the Vite dev server, Tailwind utility classes.

**M2 — Read real data (Items).**
Enable CORS on the Spring API, then replace the hard-coded rows with a `fetch` to `GET /api/items` inside a `useEffect`, rendering the live list. Handle loading and empty/error states. *Learn:* `useEffect`, async data fetching, the request/response lifecycle, why CORS exists.

**M3 — Create data (forms → POST).**
Build a "New Item" form that POSTs to `/api/items`, and surface your API's validation errors — when the backend returns a `400` with `{field: message}`, show those messages inline under the fields. This is where your Project 1 M4 error handling pays off visually. *Learn:* controlled inputs, form submission, handling non-200 responses, mapping API errors to the UI.

**M4 — Shipments + allocation view.**
A form to build a shipment with lines (pick items, set quantities/weights/costs and an allocation method), POST it, then call `GET /api/shipments/{id}/allocation` and render the landed-cost results in a table. *Learn:* more complex forms (dynamic line rows), composing multiple API calls, presenting computed results.

**M5 — Polish + a chart.**
Add an allocation-method toggle so users can see how VALUE vs. WEIGHT vs. QUANTITY changes the split, plus a small bar chart (Recharts) of allocated cost per line. Tidy the layout into a real dashboard. *Learn:* derived UI state, data visualization, UX refinement.

**M6 — Deploy full-stack.**
Deploy the API container to Render/Railway and the React app to Vercel, point the frontend at the deployed API URL via an environment variable, and fix production CORS. README with a live link and screenshots. *Learn:* environment variables for API base URLs, build vs. dev, production CORS, reading deploy logs.

### Stretch goals
- Items **edit/delete** (PUT/DELETE) for full CRUD from the UI.
- A **shipments history** page listing past shipments and their allocations.
- **Loading skeletons** and toast notifications for a polished feel.
- Wire in **auth** once you add Spring Security to Project 1.

### Resume line (once M6 is done)
> Built and deployed a React (Vite) + Tailwind dashboard front-ending a Spring Boot landed-cost API, with live data fetching, form validation surfaced from the API, an interactive allocation-method comparison, and a deployed full-stack demo. Live link + source on GitHub.

---

## Project 3 — "AppTrack" (React + Node/Express full-stack, from scratch)

A job-application tracker web app — a clean full-stack CRUD app with a real UI, where you build **both halves yourself**. **You'll actually use it** (you're job hunting right now), which keeps motivation high, and it's directly demoable: you can send a hiring manager a live link.

**Why this one:** where Project 2 puts a frontend over an API you already had, Project 3 has you write the backend *from scratch* in a different stack (Node/Express) — so you prove you can stand up a full application end to end, not just a UI. The domain is simple on purpose so the *engineering* is the star. That's the exact skill set junior-to-mid full-stack listings screen for.

### Stack
- **Frontend:** React 18 + **Vite**
- **Styling:** Tailwind CSS
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
Separate backend: Express server with REST endpoints (`GET/POST/PUT/DELETE /api/applications`), data in an in-memory array first. *Learn:* Express routing, middleware, `req`/`res`, CORS, REST conventions — notice how much mirrors what you did in Java for Project 1.

**M4 — Wire frontend to backend.**
Replace local state with `fetch` calls to your API. Handle loading and error states. *Learn:* `useEffect`, async data fetching, the request/response lifecycle (you'll already know this from Project 2 — reinforce it).

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

**Prefer depth over breadth.** A couple of finished, deployed, documented projects beat five half-built ones. Finish each project through M6 before starting the next if time is tight.

**Suggested pace:** Project 1 is done. Project 2 next — it's lighter, since the backend already exists and you're focused purely on the frontend and wiring. Then Project 3 when you want to build a full stack from scratch. Adjust to reality — done-and-shipped is the only metric that matters.

**When you're ready to build**, I can scaffold either repo for you (folder structure, boilerplate, first working component), pair through any milestone, review your code, or help debug. Just say the word and which project.
