# Shop & Products Microservices (Spring Boot)

A small two-service system to demo **JWT-secured shopping APIs** with **catalog re-pricing** via Feign, **payments using Strategy**, and a **Checkout Facade** that orchestrates order creation + payment in one call.

---

## Tech Stack

* **Java 17**, **Maven 3.9+**
* **Spring Boot 3.5.4**
* **Spring Security 6** + **JJWT 0.11.5** (Bearer JWT)
* **springdoc-openapi 2.6.0** (Swagger UI)
* **Spring Data JPA** + **H2 (in-memory)**
* **Spring Cloud OpenFeign** (Release Train **2025.0.0**) for inter-service calls

---

## Architecture

```
/products-service  (port 8081)
  - Proxies https://fakestoreapi.com
  - GET /api/products
  - GET /api/products/{id}
  - Swagger: /swagger

/shop-service      (port 8080)
  - JWT auth (in-memory users)
  - Customers CRUD:      /api/customers
  - Orders CRUD (repriced with catalog): /api/orders
  - Payments (Strategy): /api/payments
  - Checkout (Facade):   /api/checkout  => create order -> pay -> return final status
  - Swagger: /swagger
  - H2 Console: /h2 (jdbc:h2:mem:shopdb)
```

**Users (in-memory):**

* `admin / admin123` (ROLE\_ADMIN)
* `user  / user123`   (ROLE\_USER)

---

## Project layout

```
.
├── products-service
│   └── src/main/java/com/proxyproject/products_service
│       ├── controllers/ProductController.java
│       └── clients/FakeStoreClient.java
└── shop-service
    └── src/main/java/com/proxyproject/shop
        ├── auth (login, JWT)
        ├── customers (CRUD)
        ├── orders (domain, repo, service, pricing)
        ├── payments (Strategy + service)
        ├── checkout (facade + web)
        ├── products (Feign client)
        └── config/security/openapi
```

---

## Quickstart

### 1) Requirements

* Java 17
* Maven 3.9+
* Ports **8080** and **8081** free

### 2) Run **products-service** (port 8081)

```bash
cd products-service
mvn spring-boot:run
```

* Swagger: [http://localhost:8081/swagger](http://localhost:8081/swagger)
* API docs: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

### 3) Run **shop-service** (port 8080)

```bash
cd shop-service
mvn spring-boot:run
```

* Swagger: [http://localhost:8080/swagger](http://localhost:8080/swagger)
* H2 Console: [http://localhost:8080/h2](http://localhost:8080/h2)

  * JDBC URL: `jdbc:h2:mem:shopdb`
  * User: `sa`  (no password)

> In **`shop-service/src/main/resources/application.properties`** the products base URL must point to products-service:

```properties
# Swagger/OpenAPI
springdoc.swagger-ui.path=/swagger
springdoc.api-docs.path=/v3/api-docs

# JWT
app.jwt.secret=change-this-in-prod-very-long-secret-string-1234567890
app.jwt.exp-minutes=60

# H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2
spring.jpa.hibernate.ddl-auto=create-drop

# Feign target (catalog)
products.api.base-url=http://localhost:8081

# Optional Feign logging while developing
feign.client.config.products.loggerLevel=full
logging.level.com.proxyproject.shop.products=DEBUG
```

---

## Security

* Public paths: `/auth/**`, `/api/auth/**`, `/v3/**`, `/swagger-ui/**`, `/swagger`, `/h2/**`, and `GET /api/products/**`
* Everything else requires `Authorization: Bearer <JWT>`

Login to get a token:

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{ "username": "admin", "password": "admin123" }
```

Response: `{ "accessToken": "..." }`

Use it on secured endpoints:

```
Authorization: Bearer <JWT>
```

---

## API Summary (shop-service)

* **Customers**: `GET/POST/PUT/DELETE /api/customers[/{id}]`
* **Orders**:

  * `POST /api/orders` → creates order with items
  * `GET /api/orders/{id}`, `GET /api/orders`
  * `PUT /api/orders/{id}/cancel`
  * **Re-pricing**: when creating an order the service **ignores client prices** and fetches current prices from **products-service**.
* **Payments** (Strategy):

  * `POST /api/payments` → validates amount == order.total, and state transitions to **PAID**.
  * Blocks paying **CANCELLED/PAID** orders (409).
* **Checkout** (Facade):

  * `POST /api/checkout` → creates order (repriced) and pays in one call.
    If `amount` is sent and does not match order total → **400**.

---

## Feign: Catalog Re-pricing

`OrderPricingService` calls **products-service** (`/api/products/{id}`) via Feign to fetch **real prices** on each order creation.
This prevents client-side tampering.

---

## Payments (Strategy Pattern)

* Strategies provided: `CREDIT_CARD`, `PAYPAL`, `CRYPTO (optional)`
* `PaymentService` enforces:

  * 400 if `amount` is missing or != `order.total`
  * 409 if order is `CANCELLED` or already `PAID`
  * On success, order goes to **PAID** and a `PaymentResponse` is returned

---

## Checkout (Facade)

`CheckoutFacade` orchestrates:

1. Create order (items repriced using products-service)
2. Determine `amount` (request or order.total)
3. Pay via `PaymentService`
4. Return final order/payment status

---

## Swagger & H2

* **products-service** Swagger:
  `http://localhost:8081/swagger`
* **shop-service** Swagger:
  `http://localhost:8080/swagger`
* **shop-service** H2 Console:
  `http://localhost:8080/h2` (JDBC URL: `jdbc:h2:mem:shopdb`)

---

## Postman

Import the two JSON files:

* `Shop.postman_collection.json`
* `Shop.local.postman_environment.json`

Use **Auth → Login** first; the test script stores `{{token}}` automatically.
Requests included: Customers, Orders, Payments, Checkout, and Products proxy.

---

## cURL Examples

### Login

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "username": "admin", "password": "admin123" }'
```

### Create Order (client prices are ignored)

```bash
curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      { "productId": 1, "quantity": 1, "price": 0.01 },
      { "productId": 2, "quantity": 2, "price": 9999.99 }
    ]
  }'
```

### Checkout (valid)

```bash
curl -s -X POST http://localhost:8080/api/checkout \
  -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [ { "productId": 1, "quantity": 1 } ],
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Checkout (invalid amount → 400)

```bash
curl -s -X POST http://localhost:8080/api/checkout \
  -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [ { "productId": 1, "quantity": 1 } ],
    "paymentMethod": "CREDIT_CARD",
    "amount": 1.00
  }'
```

---

## Troubleshooting

* **Swagger 500 on `/v3/api-docs`**
  Make sure **springdoc 2.6.0** is pinned and `spring-boot-starter-validation` is present (for Jakarta Validation provider).

* **Swagger or H2 returning 401**
  Security must `permitAll` for `/v3/**`, `/swagger-ui/**`, `/swagger`, and `/h2/**`.
  The JWT filter should bypass these public paths and **not** send 401 when there is no token.

* **Feign not hitting products-service**
  Confirm `products.api.base-url=http://localhost:8081` and that products-service is running.

* **Amount mismatch**
  Payments return **400** when `amount` != `order.total`. Use checkout without `amount` to auto-use the order total.

---

## Build

```bash
# products-service
cd products-service
mvn clean package

# shop-service
cd ../shop-service
mvn clean package
```

Run with:

```bash
mvn spring-boot:run
```

---

## Notes

* JWT secret is hardcoded in `application.properties` **only for demo**. Use environment variables/secret manager in real environments.
* H2 is in-memory (`create-drop`); data resets on restart.
* Idempotency for checkout can be added with a `clientReferenceId` persisted alongside the order/payment record.

---

## License

MIT (or adapt to your needs).
