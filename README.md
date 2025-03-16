# Customer Phone Number Management API

## API Overview
This is a Spring Boot application providing a REST API (WebFlux) to manage phone numbers for customers. 
The API supports basic retrieving phone numbers, including pagination.
Also, activation, and deactivation of phone numbers.

## Features
- **Get all phone numbers** with pagination.
- **Get all phone numbers of a specific customer**.
- **Activate/deactivate a given phone number**.
- **Input validation and error handling.**
- **Support for handling large data volumes with pagination.**

## Database Schema
- Two main entities: `Customer` and `PhoneNumber`. 
- Their `relationship is 1:M` (one customer can have many phone numbers), we can structure it as:

```sql
Customer:
id (primary key)
name (string, optional)

PhoneNumber:
id (primary key)
customer_id (foreign key to Customer)
phone_number (string)
active (boolean to indicate whether the phone number is active or not)
```

## Endpoints

### 1. Get all phone numbers (Paginated)

**GET** `/phone-numbers`

**Parameters**:
- `page` (optional, default: 0): The page number to retrieve.
- `size` (optional, default: 10): The number of phone numbers per page.

**Response**:
```json
{
  "contents": [
    {
      "id": 1,
      "number": "0412334423",
      "active": true,
      "customer_id": 1
    },
    ...
  ],
  "page": 1,
  "size": 10,
  "total_pages": 1
  "total_results": 3
}
```

### 2. Get all phone numbers of a customer
**GET** `/customers/{customer_id}/phone-numbers`

**Path parameters**:
- `customer_id` (required): The customer id of the phone numbers to retrieve.

**Response**:
```json
[
  {
    "id": 1,
    "number": "0412334423",
    "active": true,
    "customer_id": 1
  },
  ...
]
```

### 3. Activate a phone number
**GET** `/phone-numbers/{phone_number_id}/activation`

### 4. Deactivate a phone number
**GET** `/phone-numbers/{phone_number_id}/deactivation`

**Path parameters**:
- `phone_number_id` (required): The phone number id to activate or deactivate.

**Request payload**:
```json
{
  "active": true|false
}
```

**Response**:
```json
{
  "id": 1,
  "number": "0412334423",
  "active": true|false,
  "customer_id": 1
}
```

## Error Handling
The API uses standard HTTP status codes for error handling:

- 400 Bad Request: Invalid input, missing parameters, or invalid query parameters.
- 404 Not Found: Customer or phone number not found.
- 500 Internal Server Error: When unexpected error has occurred.

### Example Error Response:
**Response**:
```json
{
  "message": "Customer ID not found."
}
```

### Validation
- Customer IDs must be numeric.
- Phone number IDs must be numeric.
- Pagination page size must be greater than one.
- Pagination page number must be greater than zero.
- Phone number activation/deactivation allowed only if database status is different to requested state.

## Testing
### Repository layer
 - Written using @DataJpaTest to test database interactions.
 - Embedded H2 database used for testing.

### Service layer
 - Written using @ExtendWith(MockitoExtension.class), @Mock and Mockito to test service in isolation.
  
### Controller layer
 - Written using @WebFluxTest to test the controller in isolation.
 - Mock service dependencies.


### You can run the tests using:

```bash
./mvnw test verify
```

### Tests cover various scenarios, including:

- Valid and invalid pagination.
- Activation and deactivation of phone numbers.
- Handling of missing or invalid query parameters.

## Getting Started
### Prerequisites
- Java 17 or higher
- Maven

### Setup

#### Clone the repository:
```bash
git clone https://github.com/your-username/phone-number-api.git
cd phone-number-api
```

#### Install dependencies:

```bash
./mvnw clean install
```

#### Run the application:

#### Locally (active.profile=local)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
The application will run on `http://localhost:8080` by default.

#### Try the application using Postman:
- Import the postman collection to your local Postman

#### Try the application using Swagger UI
- After running the application, you can accesss SwaggerUI on `http://localhost:8080/swagger-ui.html`

#### Actuator endpoint
**GET** `http://localhost:8080/actuator`

## TODO: Improvements
- Introduce distributed caching for better scalling.
- Logging for monitoring and alerting.
- API security
- Code linters (PMD, SpotBugs, Sonar)