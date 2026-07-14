# Landed Cost API

A REST API for inventory management and **landed-cost allocation** — the practice of spreading a shipment's overhead costs (freight, duty, insurance) across its line items to determine the true, fully-loaded unit cost of each product.

Built with Spring Boot as a hands-on project to model a real inventory/costing problem with production-style patterns: layered architecture, `BigDecimal` money math, bean validation, global error handling, containerized Postgres, and an automated test suite.

## What it does

When goods are imported, the price paid to the supplier is only part of the real cost. Freight, customs duty, and insurance all add to what an item actually costs to land in the warehouse. This API models inventory items and shipments, then runs an **allocation engine** that distributes those overhead costs across the shipment's lines by one of three methods:

- **VALUE** — allocate in proportion to each line's extended value (`unit cost × quantity`)
- **WEIGHT** — allocate in proportion to each line's weight
- **QUANTITY** — allocate in proportion to each line's quantity

The engine uses a **running-total technique** so the allocated pennies always sum back to the exact overhead pool — no lost or extra cent from rounding, which is a common bug in naive implementations.

## Tech stack

- **Java 21** (Temurin OpenJDK)
- **Spring Boot 4** — Web MVC, Data JPA, Validation
- **PostgreSQL 16** (via Docker) for persistence; H2 available for lightweight runs
- **Maven** (via the included `./mvnw` wrapper)
- **JUnit 5 + Mockito** for testing
- **springdoc-openapi** for live Swagger API documentation

## Getting started

### Prerequisites

- Java 21+
- Docker Desktop (for the PostgreSQL database)

### 1. Clone the repo

```bash
git clone https://github.com/MattNeill1/Landed-Cost-API.git
cd Landed-Cost-API
```

### 2. Configure environment variables

Database credentials are kept out of source control. Create a `.env` file in the project root (see `.env.example`):

```
POSTGRES_USER=landed
POSTGRES_PASSWORD=landed
DB_USER=landed
DB_PASSWORD=landed
```

### 3. Start the database

```bash
docker compose up -d
```

This starts PostgreSQL in a container using the values from `.env`. Data persists across restarts via a named Docker volume.

### 4. Run the application

Spring reads the database credentials from environment variables, so export them in your shell before starting the app:

```bash
export DB_USER=landed
export DB_PASSWORD=landed
./mvnw spring-boot:run
```

The API is now available at `http://localhost:8080`.

## API documentation

Interactive Swagger UI is generated automatically from the controllers:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI spec (JSON):** http://localhost:8080/v3/api-docs

You can explore and call every endpoint directly from the Swagger page.

## Endpoints

### Health

| Method | Path          | Description        |
| ------ | ------------- | ------------------ |
| GET    | `/api/health` | Service heartbeat  |

### Items

| Method | Path              | Description          |
| ------ | ----------------- | -------------------- |
| POST   | `/api/items`      | Create an item       |
| GET    | `/api/items`      | List all items       |
| GET    | `/api/items/{id}` | Get one item         |
| PUT    | `/api/items/{id}` | Update an item       |
| DELETE | `/api/items/{id}` | Delete an item       |

**Create an item:**

```json
POST /api/items
{
  "sku": "WIDGET-01",
  "description": "Steel widget",
  "unitCost": 10.00,
  "quantityOnHand": 100
}
```

### Shipments

| Method | Path                          | Description                             |
| ------ | ----------------------------- | --------------------------------------- |
| POST   | `/api/shipments`              | Create a shipment with its lines        |
| GET    | `/api/shipments`              | List all shipments                      |
| GET    | `/api/shipments/{id}/allocation` | Run the landed-cost allocation engine |

**Create a shipment:**

```json
POST /api/shipments
{
  "shipmentNumber": "SHIP-001",
  "freightCost": 100.00,
  "dutyCost": 0,
  "insuranceCost": 0,
  "allocationMethod": "VALUE",
  "shipmentLines": [
    { "item": { "id": 1 }, "quantity": 6, "weight": 5.0 },
    { "item": { "id": 2 }, "quantity": 4, "weight": 3.0 }
  ]
}
```

**Get the allocation** for a shipment (`GET /api/shipments/1/allocation`):

```json
[
  { "itemSku": "WIDGET-01", "allocatedCost": 60.00, "landedUnitCost": 20.0000 },
  { "itemSku": "GADGET-02", "allocatedCost": 40.00, "landedUnitCost": 20.0000 }
]
```

`allocatedCost` is the share of overhead assigned to that line; `landedUnitCost` is the item's base unit cost plus its per-unit share of the overhead.

## Validation & error handling

Inputs are validated with Jakarta Bean Validation (e.g. `sku` is required, `unitCost` and `quantityOnHand` must be zero or positive). A global `@RestControllerAdvice` translates failures into clean JSON:

- **Validation errors** → `400` with a `{ field: message }` map
- **Business-rule errors** (e.g. a shipment with a zero allocation basis) → `400` with an `{ error }` message
- **Unexpected errors** → `500` with a safe generic message (no stack traces leaked to clients)

## Testing

Run the fast, isolated unit tests (no database required):

```bash
./mvnw test -Dtest=AllocationServiceTest,ItemControllerTest
```

- `AllocationServiceTest` — pure JUnit tests of the allocation math, including a penny-rounding test that proves the running-total split sums to the pool exactly.
- `ItemControllerTest` — Mockito tests of the controller's 200/404 logic with a mocked repository.

> **Note:** the full `./mvnw test` also runs a `@SpringBootTest` context-load check, which boots the whole application and therefore needs PostgreSQL running and the `DB_USER` / `DB_PASSWORD` environment variables exported.

## Project status

This is a personal learning project built milestone by milestone:

- [x] **M1** — Health endpoint
- [x] **M2** — Items CRUD
- [x] **M3** — Shipments + landed-cost allocation engine
- [x] **M4** — Validation + global error handling
- [x] **M5** — PostgreSQL via Docker + JUnit/Mockito tests
- [x] **M6** — OpenAPI/Swagger docs, README _(Dockerfile optional)_

## License

Released for portfolio and educational purposes.
