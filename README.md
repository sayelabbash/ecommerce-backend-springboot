# 🛒 E-Commerce Backend System

A secure and scalable **E-Commerce Backend REST API** built using **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, MySQL, and Razorpay**.

The application provides complete backend functionality for an online shopping platform, including user authentication, product and category management, shopping cart, order processing, payments, reviews, wishlist, and administrative operations.

---

## 🚀 Features

### 👤 User Management

* User registration
* User login
* JWT-based authentication
* Password encryption using BCrypt
* Email verification
* Forgot password
* Reset password
* Update user profile
* Role-based authorization
* `USER` and `ADMIN` roles

### 🔐 Security

* Spring Security
* JWT authentication
* Stateless authentication
* Password hashing with BCrypt
* Role-based access control
* Protected REST endpoints
* Authentication filter for JWT validation

### 📦 Product Management

* Create products
* Update products
* Delete products
* Retrieve products
* Product search
* Category-based filtering
* Price range filtering
* Stock availability filtering
* Sorting
* Pagination
* Product details

### 🗂️ Category Management

* Create categories
* Update categories
* Delete categories
* Retrieve categories
* Associate products with categories

### 🛒 Shopping Cart

* Add products to cart
* Update cart quantity
* Remove products from cart
* View cart
* Calculate cart total
* Stock validation

### 📋 Order Management

* Place orders
* View order details
* View order history
* Track order status
* Cancel orders
* Restore product stock when applicable
* Transactional order processing

### 💳 Payment

* Razorpay payment integration
* Create payment orders
* Verify Razorpay payments
* Track payment status
* Support for Cash on Delivery
* Payment information associated with orders

### ⭐ Product Reviews & Ratings

* Add product reviews
* Add ratings
* View product reviews
* Prevent duplicate reviews for the same product
* Delete reviews according to authorization rules

### ❤️ Wishlist

* Add products to wishlist
* View wishlist
* Remove products from wishlist

### 👨‍💼 Admin Features

* Admin authentication
* Product management
* Category management
* Order management
* User-related administrative operations
* Dashboard/statistics information

### ⚠️ Validation & Exception Handling

* Request validation using Bean Validation
* Centralized exception handling
* Custom exceptions
* Resource-not-found handling
* Invalid request handling
* Insufficient stock handling
* Validation error responses
* Consistent API error responses

---

## 🏗️ Architecture

The application follows a layered Spring Boot architecture:

```text
Client
   │
   ▼
Controller Layer
   │
   ▼
DTO Layer
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
JPA / Hibernate
   │
   ▼
MySQL Database
```

### Main Layers

**Controller**

Handles HTTP requests and API responses.

**DTO**

Separates API request/response models from database entities.

**Service**

Contains business logic and transaction management.

**Repository**

Handles database operations using Spring Data JPA.

**Entity**

Represents database tables and relationships.

**Security**

Handles JWT authentication, authorization, and user identity.

**Exception**

Provides centralized and consistent error handling.

---

## 🛠️ Technology Stack

| Technology         | Purpose                         |
| ------------------ | ------------------------------- |
| Java 17            | Programming language            |
| Spring Boot 3      | Backend framework               |
| Spring Security    | Authentication & authorization  |
| JWT                | Stateless authentication        |
| Spring Data JPA    | Data persistence                |
| Hibernate          | ORM                             |
| MySQL              | Relational database             |
| Maven              | Build and dependency management |
| BCrypt             | Password encryption             |
| Razorpay           | Payment integration             |
| Jakarta Validation | Request validation              |
| Git & GitHub       | Version control                 |

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/sayel/E_Commerce/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/sayel/E_Commerce/
```

---

## 🔑 Environment Variables

Sensitive configuration is **not committed to the repository**.

Create a local `.env` file using `.env.example` as a reference.

Example:

```env
DB_URL=jdbc:mysql://localhost:3306/ecommerce
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_jwt_secret

RAZORPAY_KEY=your_razorpay_key
RAZORPAY_SECRET=your_razorpay_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_app_password

CORS_ALLOWED_ORIGINS=*

ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=your_admin_password
```

> Never commit `.env`, passwords, API secrets, JWT secrets, or email credentials to GitHub.

---

## 🗄️ Database

The application uses **MySQL** with Spring Data JPA and Hibernate.

Create the database before running the application:

```sql
CREATE DATABASE ecommerce;
```

The application uses Hibernate's schema update configuration to create/update the required tables.

---

## ▶️ How to Run

### 1. Clone the repository

```bash
git clone https://github.com/sayelabbash/ecommerce-backend-springboot.git
```

### 2. Navigate to the project

```bash
cd ecommerce-backend-springboot
```

### 3. Configure environment variables

Create a `.env` file using `.env.example` and provide your local configuration.

### 4. Start the application

Using Maven:

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

**Windows**

```bash
mvnw.cmd spring-boot:run
```

**Linux/macOS**

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

---

## 🔄 Main Application Flow

### User Authentication

```text
Register
   ↓
Email Verification
   ↓
Login
   ↓
JWT Token
   ↓
Access Protected APIs
```

### Shopping Flow

```text
Browse Products
      ↓
Search / Filter
      ↓
Add to Cart
      ↓
Update Cart
      ↓
Place Order
      ↓
Payment
      ↓
Order Confirmation
      ↓
Order History
```

### Review Flow

```text
User
 ↓
Select Product
 ↓
Submit Rating & Review
 ↓
Review Validation
 ↓
Store Review
```

---

## 🔐 API Security

Protected endpoints require a valid JWT token.

Use the following authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

Administrative operations require the appropriate admin role.

---

## 📌 Key API Resources

The backend exposes REST APIs for:

```text
Authentication
Users
Products
Categories
Cart
Orders
Payments
Reviews
Wishlist
Admin
```

The controllers inside the project contain the complete endpoint definitions and request/response models.

---

## 💡 Important Business Rules

* Passwords are never stored as plain text.
* JWT is used for authenticated API access.
* Users cannot access resources belonging to other users without authorization.
* Product stock is validated before order placement.
* Orders are processed transactionally.
* Product reviews are associated with users and products.
* Duplicate reviews for the same product are prevented.
* Administrative operations require appropriate authorization.

---

## 🧪 Testing

The project includes Spring Boot test configuration.

Additional API testing can be performed using tools such as:

* Postman
* Insomnia
* Swagger/OpenAPI, if configured
* Browser/API clients

---

## 🎯 Internship Task Alignment

This project implements and extends the requirements of:

### Task 5 — E-Commerce Backend

| Requirement                 | Implementation |
| --------------------------- | -------------- |
| User registration and login | ✅              |
| Product management          | ✅              |
| Product search              | ✅              |
| Shopping cart               | ✅              |
| Order placement             | ✅              |
| Order history               | ✅              |
| Product reviews and ratings | ✅              |
| JWT authentication          | ✅              |
| Database persistence        | ✅              |

### Additional Features

* Email verification
* Password reset
* Wishlist
* Category management
* Product filtering
* Pagination
* Sorting
* Razorpay payment integration
* Cash on Delivery
* Admin dashboard/statistics
* Role-based authorization
* Global exception handling
* Validation

---

## 📈 Future Improvements

Possible future enhancements include:

* Redis caching
* Elasticsearch-based product search
* Docker containerization
* CI/CD pipeline
* Kafka-based event processing
* Microservices architecture
* Cloud deployment
* Advanced monitoring and logging
* Comprehensive integration and unit test coverage

---

## 👨‍💻 Author

**Sk Sayel Abbash**

B.Tech Computer Science & Engineering

GitHub:
https://github.com/sayelabbash

---

## 📄 License

This project is developed for educational and internship purposes.
