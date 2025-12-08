# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2025-12-09

### Added
- Automated GitHub Release creation in release workflow
- Automatic upload of GPG-signed artifacts (`.jar.asc` files) to GitHub Release
- Release workflow now automatically creates GitHub Release with CHANGELOG.md content

## [1.0.1] - 2025-12-09

### Changed
- Enhanced JavaDoc documentation with detailed examples and usage instructions
- Improved comment detection logic for better performance (using `charAt(0)` instead of `startsWith()`)
- Made `DotEnvLoader` class `final` to prevent inheritance
- Improved logging efficiency with conditional checks (`isDebugEnabled()`, `isInfoEnabled()`, `isWarnEnabled()`)
- Enhanced method parameter declarations with `final` keyword for immutability

### Added
- Comprehensive JUnit 5 test suite covering all edge cases and scenarios
- Checkstyle configuration for code style enforcement
- PMD ruleset configuration for static code analysis
- SonarCloud integration and quality gate configuration
- Enhanced README.md with detailed documentation, badges, and usage examples
- CONTRIBUTING.md with contribution guidelines and development workflow
- GitHub Actions workflows for CI/CD automation
- Maven site generation with comprehensive project reports

### Fixed
- Improved handling of edge cases in `.env` file parsing (lines starting with `=` after trimming)

## [1.0.0] - 2025-XX-XX

### Added
- Initial release of dotenv library for Java
- `DotEnvLoader.loadDotEnv()` method to load environment variables from default `.env` file
- `DotEnvLoader.loadDotEnv(Path)` method to load environment variables from custom path
- `DotEnvLoader.getEnv(String)` method to retrieve environment variables
- Support for comments in `.env` files (lines starting with `#`)
- Automatic whitespace trimming for keys and values
- Safe loading: existing System properties and environment variables are never overwritten
- SLF4J logging support for debugging and monitoring
- Comprehensive JUnit 5 test suite
- Full JavaDoc documentation
- Maven Central publication support

### Features
- Simple and lightweight library
- Safe: doesn't overwrite existing environment variables
- Supports comments in `.env` files
- Fully tested with JUnit 5
- Logging support via SLF4J
- Compatible with Java 11+

