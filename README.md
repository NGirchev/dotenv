# dotenv

[![Build Status](https://github.com/NGirchev/dotenv/workflows/CI/badge.svg)](https://github.com/NGirchev/dotenv/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ngirchev/dotenv)](https://central.sonatype.com/namespace/io.github.ngirchev)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ba880b66c6e848d7ac57505788a14d87)](https://app.codacy.com/gh/NGirchev/dotenv/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11572/badge)](https://www.bestpractices.dev/projects/11572)
[![SonarCloud Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=bugs)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=NGirchev_dotenv&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv)


A simple Java library that helps you manage application configuration securely. Instead of hardcoding sensitive information like database passwords, API keys, or server addresses directly in your code, you can store them in a `.env` file. This keeps your secrets safe, prevents accidentally committing them to version control, and makes it easy to configure your application for different environments (development, testing, production).

## Features

- 🚀 Simple and lightweight
- 🔒 Safe: doesn't overwrite existing environment variables
- 📝 Supports comments in `.env` files
- 🧪 Fully tested with JUnit 5
- 📊 Logging support via SLF4J

## Requirements

**This project is buildable using only FLOSS (Free/Libre and Open Source Software) tools.**

- **Java 11 or higher** (OpenJDK recommended - FLOSS)
- **Maven 3.6+** (Apache Maven - FLOSS)

All build tools, dependencies, and test frameworks used are FLOSS.

## Versioning

This project uses **[Semantic Versioning](https://semver.org/)** (`MAJOR.MINOR.PATCH`, e.g. `1.0.0`).  
Each release has a **unique version identifier**, is tagged in Git as `v{version}` (e.g. `v1.0.0`), and is published to Maven Central with the same version.  
Development builds use the `-SNAPSHOT` suffix (e.g. `1.0.1-SNAPSHOT`) and are not intended for production use.  
The current released version can be seen in the Maven Central badge above or on the GitHub Releases page.

## Installation

The library is available on [Maven Central](https://central.sonatype.com/namespace/io.github.ngirchev). Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.ngirchev</groupId>
    <artifactId>dotenv</artifactId>
    <version>1.0.2</version>
</dependency>
```

For Gradle users, add to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.ngirchev:dotenv:1.0.0'
}
```

After adding the dependency, rebuild your project to download the library from Maven Central.

## Getting Started

### Quick Start Tutorial

Follow these steps to get started with dotenv in your Java application:

1. **Add the dependency** to your `pom.xml` or `build.gradle` (see Installation above)

2. **Create a `.env` file** in your project root directory:

```env
# Database configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mydatabase

# API credentials
API_KEY=your-api-key-here
```

3. **Load the environment variables** at the start of your application:

```java
import io.github.ngirchev.dotenv.DotEnvLoader;

public class MyApp {
    public static void main(String[] args) {
        // Load .env file - call this before using any environment variables
        DotEnvLoader.loadDotEnv();
        
        // Now you can access the variables
        String dbHost = DotEnvLoader.getEnv("DB_HOST");
        String apiKey = DotEnvLoader.getEnv("API_KEY");
        
        System.out.println("Connecting to database at: " + dbHost);
        // Your application code here...
    }
}
```

4. **Run your application** as usual. The library will automatically load variables from the `.env` file.

**Important**: Make sure to call `DotEnvLoader.loadDotEnv()` before accessing any environment variables, ideally at the very beginning of your `main` method or in a static initializer block.

## Usage

### Custom .env File Path

```java
import java.nio.file.Paths;

// Load from custom path
DotEnvLoader.loadDotEnv(Paths.get("/path/to/custom/.env"));
```

### Spring Boot Integration

Load environment variables **before** Spring Boot starts. Use a static block in your main application class:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.ngirchev.dotenv.DotEnvLoader;

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

Public API is provided by a single class `io.github.ngirchev.dotenv.DotEnvLoader` with three static methods:

- `loadDotEnv()` – load variables from the default `.env` file in the current working directory.
- `loadDotEnv(Path envPath)` – load variables from the given `.env` file without overwriting existing System properties or environment variables.
- `getEnv(String key)` – read a value by key, first from System properties (loaded from `.env`), then from environment variables.

For full JavaDoc (including parameter and exception details), see:

- Locally generated JavaDoc: `mvn javadoc:javadoc` → `target/site/apidocs/`
- JavaDoc JAR on Maven Central for this artifact

## Security Vulnerability Reporting

### Vulnerability Report Process

**The project publishes the process for reporting security vulnerabilities on the project site.**

**Security Policy URL**: [https://github.com/NGirchev/dotenv/security/policy](https://github.com/NGirchev/dotenv/security/policy)

If you discover a security vulnerability, please report it responsibly. **Do not open a public GitHub issue** for security vulnerabilities.

### How to Report Security Vulnerabilities

**For Private Vulnerability Reports:**

This project supports **private vulnerability reporting** to allow security researchers to report vulnerabilities without making them public.

To report a security vulnerability privately:

1. Go to the [GitHub Security Advisories page](https://github.com/NGirchev/dotenv/security/advisories/new)
2. Click "Report a vulnerability"
3. Fill out the security advisory form with:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)
4. Submit the advisory

**Alternative Method (Email):**

If you prefer to report via email, you can contact the maintainer directly. However, GitHub Security Advisories is the preferred method as it provides better tracking and coordination.

### Response Time

**The project's initial response time for any vulnerability report received in the last 6 months is less than or equal to 14 days.**

You will receive an acknowledgment of your report within 14 days, which may include:
- Confirmation that the vulnerability has been received
- Request for additional information if needed
- Initial assessment of the vulnerability
- Timeline for fix and disclosure (if applicable)

### Disclosure Policy

- Vulnerabilities will be addressed promptly
- A fix will be developed and tested before public disclosure
- Security advisories will be published when fixes are available
- Credit will be given to reporters (unless they prefer to remain anonymous)

For more information, see GitHub's [Security Policy](https://github.com/NGirchev/dotenv/security/policy) page.

## Security Best Practices

When working with sensitive configuration data, follow these security guidelines:

### ✅ DO:

- **Add `.env` to `.gitignore`**: Never commit `.env` files containing secrets to version control. Add `.env` to your `.gitignore` file:
  ```
  .env
  .env.local
  .env.*.local
  ```

- **Use `.env.example` for templates**: Create a `.env.example` file with placeholder values (no real secrets) and commit it to help other developers:
  ```env
  DB_HOST=localhost
  DB_PORT=5432
  API_KEY=your-api-key-here
  ```

- **Set proper file permissions**: On Unix-like systems, restrict access to `.env` files:
  ```bash
  chmod 600 .env  # Only owner can read/write
  ```

- **Use environment variables in production**: For production deployments, prefer setting environment variables directly rather than using `.env` files, as they're more secure and easier to manage in containerized environments.

- **Validate required variables**: Always check that required environment variables are present before using them:
  ```java
  String apiKey = DotEnvLoader.getEnv("API_KEY");
  if (apiKey == null || apiKey.isEmpty()) {
      throw new IllegalStateException("API_KEY is required but not set");
  }
  ```

### ❌ DON'T:

- **Don't commit `.env` files**: Never commit files containing real secrets, passwords, or API keys to Git repositories.

- **Don't share `.env` files**: Don't email, share via chat, or store `.env` files in insecure locations.

- **Don't hardcode fallback secrets**: Avoid hardcoding default secrets in your code as fallbacks.

- **Don't log sensitive values**: Be careful not to log environment variable values that contain secrets.

- **Don't use `.env` for production secrets**: In production, use proper secret management systems (e.g., AWS Secrets Manager, HashiCorp Vault, Kubernetes Secrets) instead of `.env` files.

For more information on secure configuration management, see [OWASP Secure Coding Practices](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/).

### Secure Development Resources

For contributors and developers working on this project, the following resources provide guidance on secure development practices:

- **[OWASP Secure Coding Practices](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)** - Comprehensive guide to secure coding practices
- **[OpenSSF Secure Software Development Fundamentals](https://openssf.org/curricula/)** - Free courses on secure software development
- **[Java Secure Coding Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)** - Oracle's secure coding guidelines for Java
- **[CWE Top 25](https://cwe.mitre.org/top25/)** - Most dangerous software weaknesses
- **[OWASP Top 10](https://owasp.org/www-project-top-ten/)** - Most critical application security risks

For detailed secure development guidelines for contributors, see the [Secure Development Knowledge](https://github.com/NGirchev/dotenv/blob/master/CONTRIBUTING.md#secure-development-knowledge) section in CONTRIBUTING.md.

## Behavior

- **Safe loading**: Existing System properties and environment variables are never overwritten
- **Priority**: System properties (from `.env`) have priority over environment variables
- **Missing files**: If `.env` file doesn't exist, the method silently returns (logs debug message)
- **Invalid lines**: Lines without `=` or with empty keys are ignored

## Building

**This project is buildable using only FLOSS (Free/Libre and Open Source Software) tools.**

The project uses:
- **Maven** (Apache Maven) - FLOSS build automation tool
- **Java** (OpenJDK) - FLOSS programming language and runtime
- **Maven Compiler Plugin** - FLOSS compiler plugin
- All build dependencies are FLOSS

### Compiler Warnings

**The project enables compiler warning flags to look for code quality errors and common simple mistakes.**

The build configuration includes:
- **All compiler warnings enabled** (`-Xlint:all`)
- **Deprecation warnings enabled**
- **Warnings treated as errors** (`failOnWarning=true`) - the build fails if there are any warnings

**The project addresses all compiler warnings** to maintain high code quality. This ensures that common mistakes are caught early and best practices are followed.

### Static Code Analysis

**The project uses static code analysis tools to detect potential bugs, security vulnerabilities, and code quality issues.**

The project uses **[SpotBugs](https://spotbugs.github.io/)** for static code analysis. SpotBugs is:
- A FLOSS static analysis tool for Java
- Integrated into the Maven build process
- Automatically runs during compilation
- Detects hundreds of bug patterns including security vulnerabilities

**Static analysis is automatically executed** during the build and must pass before code can be merged. The build will fail if SpotBugs finds any issues.

To run static analysis manually:
```bash
mvn spotbugs:check
```

For more information, see the [Static Code Analysis](https://github.com/NGirchev/dotenv/blob/master/CONTRIBUTING.md#static-code-analysis) section in CONTRIBUTING.md.

### Code Style Checking

**The project uses [Checkstyle](https://checkstyle.sourceforge.io/) to enforce consistent code style.**

Checkstyle is:
- A FLOSS development tool to help programmers write Java code that adheres to a coding standard
- Based on Google Java Style Guide
- Integrated into the Maven build process
- Automatically validates code style during the build

**Code style validation is automatically executed** during the build and must pass before code can be merged. The build will fail if Checkstyle finds any style violations.

To run Checkstyle manually:
```bash
mvn checkstyle:check
```

To generate a Checkstyle report:
```bash
mvn checkstyle:checkstyle
```

The report will be available at `target/site/checkstyle.html`.

### SonarCloud Integration

**The project uses [SonarCloud](https://sonarcloud.io/) for continuous code quality inspection.**

SonarCloud provides:
- Automated code quality and security analysis
- Code coverage tracking
- Technical debt monitoring
- Quality gate enforcement

The project is automatically analyzed on every push and pull request. View the analysis results and quality metrics on [SonarCloud](https://sonarcloud.io/summary/new_code?id=NGirchev_dotenv).

To build the project:

```bash
mvn clean install
```

This will compile the source code (with strict warning checks), run tests, and package the library into a JAR file. The build will fail if there are any compiler warnings.

## Testing

### Automated Test Suite

**This project uses an automated test suite that is publicly released as FLOSS.**

The project uses **[JUnit 5](https://junit.org/junit5/)** (JUnit Jupiter) as the test framework, which is:
- Publicly released as FLOSS (licensed under the Eclipse Public License 2.0)
- Maintained as a separate FLOSS project
- Standard testing framework for Java applications

The test suite includes comprehensive unit tests covering:
- Loading environment variables from `.env` files
- Handling comments, empty lines, and whitespace
- Error handling and edge cases
- Priority of System properties over environment variables
- Non-overwriting of existing properties

### Running Tests

**The test suite is invocable in a standard way for Java projects using Maven.**

To run the automated test suite:

```bash
mvn test
```

This is the **standard Maven command** for running tests in Java projects. The command will:
1. Compile the source code
2. Compile the test code
3. Execute all tests using JUnit 5
4. Display test results

**Alternative ways to run tests:**

```bash
# Run tests with verbose output
mvn test -X

# Run a specific test class
mvn test -Dtest=DotEnvLoaderTest

# Run tests and generate coverage report
mvn test jacoco:report
```

### Code Coverage

**The project uses [JaCoCo](https://www.jacoco.org/jacoco/) for code coverage analysis.**

JaCoCo is:
- A FLOSS Java code coverage library
- Integrated into the Maven build process
- Automatically generates coverage reports during testing
- Enforces minimum coverage thresholds to maintain code quality

**Coverage requirements:**
- Minimum line coverage: 80%
- Minimum branch coverage: 70%

Coverage reports are generated automatically during the build and can be viewed at `target/site/jacoco/index.html` after running `mvn test jacoco:report`.

To check coverage thresholds:
```bash
mvn verify
```

The build will fail if coverage thresholds are not met.

### Continuous Integration

The test suite is automatically executed on every push and pull request via GitHub Actions CI pipeline (`.github/workflows/ci.yml`). The CI configuration is publicly available and uses FLOSS tools.

**CI Pipeline URL**: [https://github.com/NGirchev/dotenv/actions](https://github.com/NGirchev/dotenv/actions)

## Releasing

### Manual Release

To create a release manually:

```bash
# 1. Prepare release (version and tag)
# This creates a Git tag with format v{version} (e.g., v1.0.0)
mvn -B release:prepare -Darguments="-DskipTests"

# 2. Switch to release tag
git checkout v1.0.0

# 3. Deploy to Maven Central
mvn clean deploy -P release -DskipTests

# 4. Push changes and tags
# This pushes the Git tag to the repository, making the release identifiable in version control
git push origin master
git push --tags
```

**Note:** 
- Do NOT use `release:perform`. Use `mvn clean deploy -P release` instead.
- The `release:prepare` step automatically creates a Git tag with the format `v{version}` (e.g., `v1.0.0`) for each release, ensuring each release is identified within the version control system.

### GitHub Actions

This project uses GitHub Actions for continuous integration:

1. **CI Pipeline** (`.github/workflows/ci.yml`):
   - Runs on every push and pull request
   - Builds the project and runs all tests

Releases are performed manually from the command line (see "Manual Release" section above).

### Development History

This project maintains a complete development history in the public repository. **The repository includes interim versions for review between releases; it does NOT include only final releases.**

All commits, branches, and Pull Requests between releases are publicly available for collaborative review. This enables:
- **Transparent code review**: Contributors and reviewers can see the evolution of changes before they are included in a final release
- **Collaborative development**: All intermediate commits are visible, allowing for feedback and discussion during development
- **Complete history**: The full development process is documented, not just release snapshots

**Note**: The project may choose to omit specific interim versions from the public repository only in exceptional cases (e.g., versions that fix non-public security vulnerabilities that may never be publicly released, or include material that cannot be legally posted and are not in the final release).

## License

See [LICENSE](LICENSE) file for details.

## Reporting Issues and Suggesting Enhancements

Found a bug or have an idea for improvement? We'd love to hear from you!

**⚠️ Security Vulnerabilities**: If you discover a security vulnerability, please **do not** open a public issue. Instead, follow the [Security Vulnerability Reporting](#security-vulnerability-reporting) process to report it privately.

### Bug Reporting Process

**The project provides a process for users to submit bug reports using the GitHub Issues tracker.**

**Issue Tracker URL**: [https://github.com/NGirchev/dotenv/issues](https://github.com/NGirchev/dotenv/issues)

This project uses **GitHub Issues** as the issue tracker for tracking individual issues, bug reports, and enhancement requests. All issues are publicly accessible and searchable, providing a publicly available archive for reports and responses.

### How to Report Bugs

To submit a bug report:

1. Go to the [GitHub Issues page](https://github.com/NGirchev/dotenv/issues)
2. Click "New Issue"
3. Select "Bug Report" template (if available) or create a new issue
4. Include the following information:
   - **Description**: Clear description of the problem
   - **Steps to Reproduce**: Detailed steps to reproduce the issue
   - **Expected Behavior**: What you expected to happen
   - **Actual Behavior**: What actually happened
   - **Environment**: Java version, OS, library version, etc.
   - **Code Example**: Minimal code example that demonstrates the issue (if applicable)

**Language**: Please submit bug reports, feature requests, and comments in **English** to ensure they can be understood and addressed by the global developer community.

### Enhancement Requests

To suggest an enhancement or new feature:

1. Go to the [GitHub Issues page](https://github.com/NGirchev/dotenv/issues)
2. Click "New Issue"
3. Select "Feature Request" template (if available) or create a new issue with the `enhancement` label
4. Describe the enhancement, its use case, and potential benefits

### Response Policy

- **Bug Reports**: The project acknowledges and responds to a majority of bug reports submitted in the last 2-12 months. Responses may include confirmation, requests for additional information, or status updates. A response does not necessarily mean the bug is fixed immediately, but that it has been reviewed and tracked.

- **Enhancement Requests**: The project responds to a majority (>50%) of enhancement requests submitted in the last 2-12 months. Responses may include discussion, acceptance for future consideration, or explanation if the enhancement doesn't fit the project's scope.

### Issue Archive

**All bug reports, enhancement requests, and responses are publicly available and searchable** in the [GitHub Issues archive](https://github.com/NGirchev/dotenv/issues). This provides a complete history of:
- All reported bugs and their status
- Enhancement requests and discussions
- Responses from maintainers
- Resolution status and related commits

You can search the issue archive using GitHub's search functionality to find similar issues, check if a bug has already been reported, or review past discussions.

## Contributing

Contributions are welcome! We appreciate your help in making this project better.

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature-name`)
3. Make your changes
4. Run tests (`mvn test`)
5. Commit your changes (`git commit -am 'Add some feature'`)
6. Push to the branch (`git push origin feature/your-feature-name`)
7. Create a [Pull Request](https://github.com/NGirchev/dotenv/pulls)

### Contribution Requirements

All contributions must meet our requirements for acceptable contributions, including:

- **Coding Standards**: Follow Java code conventions and maintain consistent style
- **Testing**: Ensure all tests pass and add tests for new features
- **Documentation**: Add JavaDoc comments for public APIs
- **Code Quality**: Write clean, readable, and maintainable code

For detailed contribution requirements, coding standards, and guidelines, please see [CONTRIBUTING.md](CONTRIBUTING.md#contribution-requirements).

## Contributors

- [NGirchev](https://github.com/NGirchev) - Creator and maintainer
