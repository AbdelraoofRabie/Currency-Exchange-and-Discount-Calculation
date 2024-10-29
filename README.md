# Currency Exchange and Discount Calculation API

## Requirements
- Java 17
- Maven/Gradle
- Spring Boot

## Instructions
- To run the project, execute `mvn spring-boot:run` (or the Gradle equivalent).
- Access `/api/calculate` endpoint (authentication required).
- Run tests with `mvn test`.

## API Endpoint
- **POST /api/calculate**
- Request Body:
  ```json
  {
    "amount": 200,
    "userType": "employee",
    "tenure": 3,
    "originalCurrency": "USD",
    "targetCurrency": "EUR",
    "isGrocery": false
  }
