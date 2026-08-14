# Stall & Sundry — Backend (Spring Boot)

Your original Spring Boot e-commerce API, fixed and extended. This document
covers what changed and how to run it.

## Running it

1. Copy `.env.example` to `.env` and fill in your real values (MySQL
   credentials, a JWT secret, Razorpay test keys, a Gmail app password).
   **Your original `.env` is already included with your existing values** —
   just double check they're still current.
2. Create the MySQL database referenced by `DB_URL` if it doesn't exist yet:
   ```sql
   CREATE DATABASE ecommerce;
   ```
3. Run it:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API starts on **http://localhost:8081**.

On first startup a default admin account is created automatically (see
console output) so you can log into the admin dashboard immediately:

- Email: `admin@shopsphere.com`
- Password: `Admin@123`

Override these via `ADMIN_EMAIL` / `ADMIN_PASSWORD` in `.env`.

## Bugs fixed

- **Critical: orders/cart couldn't be created on MySQL.** `CartItem` and
  `Order` mapped their `user` field to a column literally named `user-id`
  (with a hyphen). Hibernate generates unquoted DDL, so MySQL read the
  hyphen as a minus sign and the `CREATE TABLE` statement would fail outright
  on a fresh database. Renamed to `user_id`.
- **No CORS configuration.** The API had zero CORS setup, so any separate
  frontend (like the one included here) would be blocked by the browser.
  Added a proper `CorsConfigurationSource`, configurable via
  `CORS_ALLOWED_ORIGINS`.
- **Wrong HTTP status codes everywhere.** Most "not found" and business-rule
  errors were thrown as bare `RuntimeException`, which your
  `GlobalExceptionHandler` only had a catch-all for → every error came back
  as a `500`, including things like "product not found" that should be a
  `404`. Replaced with `ResourceNotFoundException` (404),
  `BadRequestException` (400), and `InsufficientStockException` (409), each
  mapped correctly.
- **No stock enforcement.** Nothing stopped you from ordering more of a
  product than existed. Products now have a `stock` field; the cart and
  checkout validate against it, decrement it on order placement, and restore
  it if an order is cancelled.
- **Razorpay amount bug.** The amount sent to Razorpay was a raw
  `double * 100`, which can produce a non-integer paise value depending on
  floating point rounding (Razorpay requires an integer). Now rounded
  properly with `Math.round`.
- **Stray/unused imports and inconsistent exception usage** cleaned up
  across controllers and services.

## New features

- **Product search, filtering & pagination** — `GET /api/products` accepts
  `keyword`, `categoryId`, `minPrice`, `maxPrice`, `inStock`, `sortBy`
  (`price`/`rating`/`newest`/`name`), `direction`, `page`, `size`.
- **Product reviews & ratings** — one review per user per product (re-posting
  updates it), with a live-updating average rating & review count stored on
  the product.
- **Wishlist** — save/remove/list products.
- **Stock management** — see above.
- **Order cancellation** (customer, while `PENDING`) and **order status
  management** (admin: `PENDING → CONFIRMED/PAID → SHIPPED → DELIVERED`, or
  `CANCELLED`), plus shipping address/phone/payment method captured at
  checkout.
- **Cash on Delivery** as an alternative to Razorpay.
- **Forgot / reset password** flow via emailed token.
- **User profile endpoints** (`GET/PUT /api/users/me`) — previously only an
  admin-only user list existed.
- **Admin stats dashboard** endpoint (`GET /api/admin/stats`) — product,
  order, user, category counts and total revenue.
- **Richer product & category data** — brand, discount price, an image
  gallery, category description/image.
- **A public endpoint for the Razorpay key id** (`GET /api/payment/key`) so
  a frontend can actually open the Razorpay checkout widget — this was
  missing entirely before.
- **Auto-seeded admin account** on first run (see above).

## API surface (new/changed endpoints)

```
GET    /api/products?keyword=&categoryId=&minPrice=&maxPrice=&inStock=&sortBy=&direction=&page=&size=
GET    /api/products/{id}
GET    /api/products/{id}/reviews
POST   /api/products/{id}/reviews                [auth]
DELETE /api/reviews/{id}                          [auth, owner or admin]

GET    /api/wishlist                              [auth]
POST   /api/wishlist/{productId}                  [auth]
DELETE /api/wishlist/{productId}                  [auth]

POST   /api/orders            { shippingAddress, phone, paymentMethod }  [auth]
GET    /api/orders/my                             [auth]
GET    /api/orders/{id}                            [auth, owner or admin]
POST   /api/orders/{id}/cancel                      [auth, owner or admin]
PATCH  /api/orders/{id}/status  { status }           [admin]

GET    /api/users/me                                [auth]
PUT    /api/users/me           { name }               [auth]
POST   /api/auth/forgot-password { email }
POST   /api/auth/reset-password  { token, newPassword }

GET    /api/payment/key                              [public — Razorpay key id]

GET    /api/admin/stats                               [admin]
```

Existing endpoints (products/categories admin CRUD, cart, register/login/
verify, payment create/verify) are unchanged in shape except where noted
above (e.g. `ProductRequest`/`ProductResponse` now include `stock`, `brand`,
`discountPrice`, `imageUrl`, `images`).

## Security note

Your uploaded project's `.env` contains real credentials (DB password, mail
app password, Razorpay test keys). It's included as-is since it's your own
data, but it's `.gitignore`d — don't commit it if you push this to a public
repo, and rotate anything you're not sure about.
