# 🎬 Movie Ticket Booking API

## 📌 Description
A Spring Boot REST API for booking movie tickets.

## 🚀 Features
✅ Manage movies, showtimes, and tickets  
✅ JWT-based authentication  
✅ Role-based access control (Admin & Customer)  
✅ Prevent overlapping showtimes  
✅ Prevent double seat bookings  
✅ Configurable max seats per showtime  

## 📂 Setup Instructions

### 1️⃣ Prerequisites
- Java 17+
- Maven
- PostgreSQL installed

### 2️⃣ Clone & Install Dependencies
```sh
git clone https://github.com/shayh22/movie-ticket-booking.git
cd movie-ticket-booking
mvn clean install
```
### 3️⃣ Configure Database
Edit src/main/resources/application.properties:

properties
Copy
Edit
spring.datasource.url=jdbc:postgresql://localhost:5432/movies_db
spring.datasource.username=your_username
spring.datasource.password=your_password
### 4️⃣ Run the Application
sh
Copy
Edit
mvn spring-boot:run
### 5️⃣ Access the API
Swagger UI: http://localhost:8080/swagger-ui.html
### 6️⃣ API Authentication
1. Register a User
sh
Copy
Edit
curl -X 'POST' 'http://localhost:8080/api/users' -H 'Content-Type: application/json' -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER"
}'
2. Login to Get JWT Token
sh
Copy
Edit
curl -X 'POST' 'http://localhost:8080/api/auth/login' -H 'Content-Type: application/x-www-form-urlencoded' -d 'email=john@example.com&password=password123'
3. Use JWT Token in Requests
sh
Copy
Edit
curl -X 'GET' 'http://localhost:8080/api/tickets' -H 'Authorization: Bearer YOUR_JWT_TOKEN'
🛠️ Future Enhancements
✅ Refresh token support
✅ Multi-theater management
✅ Advanced seat selection

