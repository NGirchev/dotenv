# dotenv

Simplest dotenv utility for Java applications. Load environment variables from `.env` files into System properties.

## Features

- 🚀 Simple and lightweight
- 🔒 Safe: doesn't overwrite existing environment variables
- 📝 Supports comments in `.env` files
- 🧪 Fully tested with JUnit 5
- 📊 Logging support via SLF4J

## Requirements

- Java 11 or higher
- Maven 3.6+

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ru.girchev</groupId>
    <artifactId>dotenv</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

### Basic Example

1. Create a `.env` file in your project root:

```env
DB_HOST=localhost
DB_PORT=5432
API_KEY=your-secret-key
DATABASE_URL=postgresql://localhost:5432/mydb
```

2. Load the environment variables in your application:

```java
import ru.girchev.dotenv.DotEnvLoader;

public class App {
    public static void main(String[] args) {
        // Load from default .env file in project root
        DotEnvLoader.loadDotEnv();
        
        // Access variables
        String dbHost = DotEnvLoader.getEnv("DB_HOST");
        String dbPort = DotEnvLoader.getEnv("DB_PORT");
        
        System.out.println("Database: " + dbHost + ":" + dbPort);
    }
}
```

### Custom .env File Path

```java
import java.nio.file.Paths;
import ru.girchev.dotenv.DotEnvLoader;

// Load from custom path
DotEnvLoader.loadDotEnv(Paths.get("/path/to/custom/.env"));
```

### Spring Boot Integration

Load environment variables **before** Spring Boot starts. Use a static block in your main application class:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.girchev.dotenv.DotEnvLoader;

@SpringBootApplication
public class Application {

    static {
        // Load .env file before Spring Boot initialization
        // This ensures variables are available for @Value, @ConfigurationProperties, etc.
        DotEnvLoader.loadDotEnv();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Now you can use the loaded variables in your Spring configuration:

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig {
    
    @Value("${DB_HOST:localhost}")
    private String dbHost;
    
    @Value("${DB_PORT:5432}")
    private String dbPort;
    
    // Variables from .env file are now available
}
```

Or in `application.properties` / `application.yml`:

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/mydb
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

### .env File Format

The `.env` file supports:

- Key-value pairs: `KEY=value`
- Comments: lines starting with `#`
- Empty lines: ignored
- Whitespace: automatically trimmed

Example:

```env
# Database configuration
DB_HOST=localhost
DB_PORT=5432

# API keys
API_KEY=secret123
SECRET_TOKEN=abc123

# Empty lines are ignored
```

## API Reference

### `loadDotEnv()`

Loads environment variables from `.env` file in project root.

```java
DotEnvLoader.loadDotEnv();
```

### `loadDotEnv(Path envPath)`

Loads environment variables from a specific `.env` file.

**Parameters:**
- `envPath` - path to `.env` file

**Example:**
```java
DotEnvLoader.loadDotEnv(Paths.get("config/.env"));
```

### `getEnv(String key)`

Gets environment variable value. First checks System properties (loaded from `.env`), then environment variables.

**Parameters:**
- `key` - variable name

**Returns:**
- variable value or `null` if not found

**Example:**
```java
String apiKey = DotEnvLoader.getEnv("API_KEY");
```

## Behavior

- **Safe loading**: Existing System properties and environment variables are never overwritten
- **Priority**: System properties (from `.env`) have priority over environment variables
- **Missing files**: If `.env` file doesn't exist, the method silently returns (logs debug message)
- **Invalid lines**: Lines without `=` or with empty keys are ignored

## Building

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Releasing

### Manual Release

To create a release manually:

```bash
mvn release:prepare
mvn release:perform
git push origin master
git push --tags
```

### Automated Release via GitHub Actions

The project includes GitHub Actions workflows for automated releases:

1. **CI Pipeline** (`.github/workflows/ci.yml`):
   - Automatically runs on every push and pull request
   - Builds the project and runs all tests

2. **Release Pipeline** (`.github/workflows/release.yml`):
   - Manual trigger via GitHub Actions UI
   - Go to **Actions** → **Release** → **Run workflow**
   - Enter:
     - **Release version**: e.g., `1.0.1` (without SNAPSHOT)
     - **Next development version**: e.g., `1.0.2-SNAPSHOT`
   - The workflow will:
     - Run all tests
     - Prepare release (remove SNAPSHOT, create tag)
     - Build and package the release
     - Push changes and tags to repository

## License

See [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
