# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

