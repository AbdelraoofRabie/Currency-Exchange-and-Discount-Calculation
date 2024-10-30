# Currency Exchange and Discount Calculation API

## Project Overview:
- A Spring Boot REST API that calculates discounted bill amounts with currency conversion
- Implements all required features including:
  * User type-based discounts (Employee 30%, Affiliate 10%, Loyal Customer 5%)
  * Bill amount-based discounts ($5 per $100)
  * Real-time currency conversion
  * Input validation and error handling
  * Authentication and rate limiting
  * Comprehensive test coverage

Key Technical Highlights:
1. Clean Architecture with separation of concerns
2. Comprehensive test suite including unit, and integration tests
3. Input validation using Jakarta Bean Validation
4. Exception handling with custom exceptions
5. Rate limiting using Resilience4j
6. Caching for currency exchange rates
7. Logging using SLF4J
8. Code quality checks (SpotBugs, Checkstyle, PMD)

## Requirements
- Java 17
- Maven
- Spring Boot

## Instructions
- To run the project, execute `mvn spring-boot:run`.
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

### Run Tests
- Run tests with `mvn test`.

### Run Project
- Run project with `mvn spring-boot:run`.


### Generate Test Coverage Report
- Generate test coverage report with `mvn clean test jacoco:report`.
- View report in `target/site/jacoco/index.html`. 

### Run Static Code Analysis
- Run static code analysis with `mvn checkstyle:checkstyle`.
- View report in `target/site/checkstyle.html`. 

### Run all checks
- Run all checks with `mvn clean verify`.

### Run individual checks
- `mvn spotbugs:check` # SpotBugs analysis
- `mvn checkstyle:check` # Checkstyle analysis
- `mvn pmd:check` # PMD analysis
- `mvn jacoco:check` # Test coverage analysis

### Generate All Reports
- Generate all reports with `mvn site`.

Reports will be available in the `target/site` directory.

## Code Quality Checks

The build includes several code quality checks:

1. **JaCoCo** - Code coverage (minimum 80% required)
2. **SpotBugs** - Bug pattern detection
3. **Checkstyle** - Code style checks using Google Java Style
4. **PMD** - Code quality rules and copy-paste detection

## Continuous Integration

The project is configured to fail the build if:
- Code coverage is below 80%
- Any SpotBugs issues are found
- Code style violations are detected
- PMD rules are violated

