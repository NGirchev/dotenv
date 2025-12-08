# Contributing

Thank you for your interest in contributing to this project! We welcome any contributions.

**Language**: This project uses **English** for all documentation, code comments, commit messages, and issue discussions. Please submit all contributions, bug reports, and comments in English to ensure they can be understood and reviewed by the global developer community.

## Development

### Requirements

- Java 11 or higher
- Maven 3.6+

### Setup

```shell
git clone https://github.com/YOUR_USERNAME/dotenv.git
cd dotenv
mvn clean install
```

### Making Changes

1. Create a branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Run tests: `mvn test`
4. Commit and push: `git push origin feature/your-feature-name`
5. Create a Pull Request on GitHub

**Note**: The project uses only FLOSS tools for building and testing (Maven, JUnit 5, OpenJDK). All tests must pass before submitting a Pull Request.

**Development Workflow**: This project maintains a complete development history in the repository. **The repository includes interim versions for review between releases; it does NOT include only final releases.**

All commits, branches, and Pull Requests are publicly available for collaborative review. This enables:
- **Transparent code review**: Contributors and reviewers can see the evolution of changes before they are included in a final release
- **Collaborative development**: All intermediate commits are visible, allowing for feedback and discussion during development  
- **Complete history**: The full development process is documented, not just release snapshots

**Note**: The project may choose to omit specific interim versions from the public repository only in exceptional cases (e.g., versions that fix non-public security vulnerabilities that may never be publicly released, or include material that cannot be legally posted and are not in the final release).

## Release Process

_Note: Only maintainers can publish releases._

### Versioning

This project uses [Semantic Versioning](https://semver.org/) (SemVer). Each release has a **unique version identifier** that is:

- **Format**: `MAJOR.MINOR.PATCH` (e.g., `1.0.0`)
- **Git Tags**: Each release is tagged in Git with the format `v{version}` (e.g., `v1.0.0`)
- **Maven Central**: Published artifacts include the version identifier in their coordinates
- **Uniqueness**: Each version identifier is used exactly once and never reused

The version identifier serves as a unique reference point for:
- Identifying specific releases
- Tracking changes between versions
- Reproducing builds
- Reporting bugs against specific versions

### Prepare Release (version and tag)

```shell
# Interactive (will prompt for versions)
mvn -B release:prepare -Darguments="-DskipTests"

# Or specify versions explicitly
mvn -B release:prepare \
  -DreleaseVersion=1.0.0 \
  -DdevelopmentVersion=1.0.1-SNAPSHOT \
  -Darguments="-DskipTests"
```

This will:
- Remove `-SNAPSHOT` from current version
- **Create a Git tag** with the unique version identifier (format: `v{version}`, e.g., `v1.0.0`) - this identifies each release within the version control system
- Update version to next development version

**Git Tags**: Each release is automatically tagged in Git using the format `v{version}`. These tags are pushed to the repository and can be viewed using `git tag -l` or on the [GitHub Releases](https://github.com/NGirchev/dotenv/releases) page.

#### 2. Switch to Release Tag

```shell
git checkout v1.0.0
```

#### 3. Deploy to Maven Central

```shell
mvn clean deploy -P release -DskipTests
```

**Important:** Do NOT use `release:perform`. Use `mvn clean deploy -P release` instead.

### Staging Repository

After deploy, close and release from [Sonatype Central Portal](https://central.sonatype.com/):

1. Navigate to Staging Repositories
2. Select repository (look for `io.github.ngirchev`)
3. Click `Close`
4. Click `Release`

### GitHub Release

After successful deployment:

```shell
# Create GitHub release
gh release create v1.0.0 -F CHANGELOG.md

# Attach signed artifacts (for OpenSSF Security Score)
gh release upload v1.0.0 target/*.jar.asc --clobber
```

## GPG Setup

### Configure GPG

Add to `~/.gnupg/gpg.conf`:

```
auto-key-retrieve
no-emit-version
keyserver https://keys.openpgp.org
```

### Publish GPG Key

```shell
# Send public key (recommended: keys.openpgp.org)
gpg --keyserver https://keys.openpgp.org --send-keys YOUR_KEY_ID

# Verify key is published
gpg --keyserver https://keys.openpgp.org --search-keys YOUR_KEY_ID
```

### Configure Maven settings.xml

Add configuration to **global** `~/.m2/settings.xml` (not in project directory):

**1. OSSRH Server Credentials (for Central Portal):**

Get your credentials from Central Portal:
1. Log in to [Central Portal](https://central.sonatype.com/)
2. Click on your profile → "View Account"
3. Click "Generate User Token" (or "User Token" if already generated)
4. Copy the token

```xml
<servers>
  <server>
    <id>ossrh</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <!-- Use the User Token from Central Portal as password -->
    <password>YOUR_USER_TOKEN</password>
  </server>
</servers>
```

**Note:** 
- `username` = your GitHub username (e.g., `NGirchev`)
- `password` = User Token from Central Portal (not your GitHub password!)
- You can encrypt the token: `mvn --encrypt-password`

**2. GPG Configuration:**

```xml
<settings>
  <profiles>
    <profile>
      <id>gpg</id>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <!-- Specify which key to use (your key ID) -->
        <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
        <!-- Use GPG agent (recommended) - no password in file -->
        <!-- Or use environment variable: <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase> -->
      </properties>
    </profile>
  </profiles>
</settings>
```

**Security options (choose one):**

1. **GPG Agent (Recommended)** - No password in file:
   - GPG agent will prompt for password once and cache it
   - Most secure option
   - Just run: `gpg-agent --daemon` before deployment

2. **Environment Variable** - Set before deployment:
   ```shell
   export GPG_PASSPHRASE=your_passphrase
   ```
   Then use in settings.xml: `<gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>`

3. **Maven Encrypted Password** - Encrypt password with Maven:
   ```shell
   mvn --encrypt-password
   ```
   Then use encrypted value in settings.xml

## Resources

- [Publish Guide](https://central.sonatype.com/publish/publish-guide/)
- [GPG Setup](https://central.sonatype.com/publish/requirements/gpg/)
- [Central Portal](https://central.sonatype.com/)
- [OpenSSF Security Scorecard](https://api.securityscorecards.dev/#/results/getResult) - Enter: `platform=github.com`, `org=NGirchev`, `repo=dotenv`

## Secure Development Knowledge

This project follows secure development practices to ensure the security and safety of the software. Contributors should be familiar with secure coding principles and best practices.

### Secure Coding Principles

When contributing to this project, please follow these secure development practices:

1. **Input Validation**: Always validate and sanitize inputs, especially when dealing with file paths and environment variables
2. **Error Handling**: Implement proper error handling without exposing sensitive information in error messages
3. **Least Privilege**: Follow the principle of least privilege - only request necessary permissions
4. **Defense in Depth**: Implement multiple layers of security controls
5. **Secure Defaults**: Use secure default configurations
6. **No Hardcoded Secrets**: Never commit secrets, passwords, API keys, or other sensitive information to the repository

### Security Resources

The following resources provide guidance on secure development practices:

- **[OWASP Secure Coding Practices](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)** - Comprehensive guide to secure coding practices
- **[OWASP Top 10](https://owasp.org/www-project-top-ten/)** - Most critical web application security risks
- **[CWE Top 25](https://cwe.mitre.org/top25/)** - Most dangerous software weaknesses
- **[OpenSSF Secure Software Development Fundamentals](https://openssf.org/curricula/)** - Free courses on secure software development
- **[Java Secure Coding Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)** - Oracle's secure coding guidelines for Java

### Security Considerations for This Project

When working on this project, pay special attention to:

- **File Path Security**: Validate file paths to prevent directory traversal attacks
- **Environment Variable Handling**: Ensure proper handling of environment variables without exposing sensitive data
- **System Property Security**: Be cautious when setting system properties that might affect other parts of the application
- **Logging Security**: Never log sensitive information (passwords, API keys, tokens)
- **Dependency Security**: Keep dependencies up to date and review security advisories

### Security Review Process

All code contributions are subject to security review. Maintainers will review:
- Potential security vulnerabilities
- Adherence to secure coding practices
- Proper handling of sensitive data
- Input validation and sanitization

### Reporting Security Issues

If you discover a security vulnerability in the codebase, **do not** open a public issue. Instead, follow the [Security Vulnerability Reporting](https://github.com/NGirchev/dotenv/blob/master/README.md#security-vulnerability-reporting) process outlined in the README.

## Contribution Requirements

### Code Style

All contributions must follow these coding standards:

- **Java Conventions**: Follow the [Oracle Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) and standard Java naming conventions
- **Language**: All code comments, JavaDoc, variable names, and commit messages must be in **English**
- **Naming**: Use meaningful variable and method names that clearly express their purpose
- **Comments**: Add JavaDoc comments for public methods and classes. Include comments for complex logic where necessary. All comments must be in English
- **Formatting**: Maintain consistent indentation (4 spaces) and formatting throughout the codebase
- **Testing**: Ensure all tests pass (`mvn test`). New features should include appropriate unit tests
- **Code Quality**: Write clean, readable, and maintainable code

### Testing Policy

**The project has a general policy that as major new functionality is added to the software, tests of that functionality MUST be added to the automated test suite.**

This policy applies to:
- New public methods and classes
- New features and functionality
- Bug fixes (regression tests)
- Edge cases and error handling

**Evidence of adherence**: The project maintains comprehensive test coverage. For example, the `DotEnvLoader` class has corresponding tests in `DotEnvLoaderTest.java` that cover all major functionality including:
- Loading environment variables from files
- Handling comments, empty lines, and whitespace
- Error handling and edge cases
- Priority of System properties over environment variables

When adding new functionality:
1. Write tests **before or alongside** the implementation (TDD approach is encouraged)
2. Ensure tests cover the happy path, edge cases, and error conditions
3. All tests must pass before submitting a Pull Request
4. Test coverage should be maintained or improved with each change

### Compiler Warnings and Code Quality

**The project enables compiler warning flags to look for code quality errors and common simple mistakes.**

The project uses:
- **Java compiler warnings**: Enabled via `-Xlint:all` flag in Maven Compiler Plugin
- **Deprecation warnings**: Enabled to catch use of deprecated APIs
- **Treat warnings as errors**: Enabled via `failOnWarning=true` to ensure warnings are addressed

**The project MUST address all compiler warnings.** Pull Requests with compiler warnings will not be accepted.

**The project is maximally strict with warnings** - all warnings are treated as errors during the build process. This ensures:
- Code quality is maintained
- Common mistakes are caught early
- Deprecated APIs are avoided
- Best practices are followed

To check for warnings locally:
```bash
mvn clean compile
```

All warnings must be resolved before submitting a Pull Request.

### Static Code Analysis

**The project uses static code analysis tools to detect potential bugs, security vulnerabilities, and code quality issues.**

The project uses **[SpotBugs](https://spotbugs.github.io/)** (successor to FindBugs) for static code analysis. SpotBugs is:
- A FLOSS static analysis tool for Java
- Actively maintained and widely used
- Capable of detecting hundreds of bug patterns
- Integrated into the Maven build process

**Static analysis is automatically run during the build process** and must pass before code can be merged.

#### Running Static Analysis

To run static code analysis locally:

```bash
# Run SpotBugs analysis
mvn spotbugs:check

# Generate HTML report
mvn spotbugs:gui
```

**All static analysis issues MUST be addressed** before submitting a Pull Request. The build will fail if SpotBugs finds any issues.

#### Static Analysis Configuration

- **Effort level**: Maximum (most thorough analysis)
- **Threshold**: Low (reports all issues, including minor ones)
- **Fail on error**: Enabled (build fails if issues are found)
- **Integration**: Runs automatically during `mvn compile` phase

#### Common Issues to Address

When SpotBugs reports issues, contributors should:
1. Review each reported issue
2. Fix legitimate problems
3. Suppress false positives with appropriate annotations if necessary
4. Document why suppression is needed

For more information about SpotBugs, see the [SpotBugs documentation](https://spotbugs.github.io/).

### Pull Request Requirements

Before submitting a Pull Request, ensure:

1. ✅ All tests pass locally (`mvn test`)
2. ✅ Code follows the style guidelines above
3. ✅ **New features include appropriate tests** (see [Testing Policy](#testing-policy))
4. ✅ JavaDoc comments are added for public APIs
5. ✅ **No compilation warnings or errors** (warnings are treated as errors - see [Compiler Warnings](#compiler-warnings-and-code-quality))
6. ✅ Commit messages are clear and descriptive

## Questions?

If you have any questions, please create an Issue on GitHub using the [GitHub Issues tracker](https://github.com/NGirchev/dotenv/issues).

For bug reports and enhancement requests, see the [Reporting Issues and Suggesting Enhancements](https://github.com/NGirchev/dotenv/blob/master/README.md#reporting-issues-and-suggesting-enhancements) section in the README.
