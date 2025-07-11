# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DeepLinkDispatch is an Android library that provides a declarative, annotation-based API to define and handle application deep links. It uses compile-time annotation processing to generate efficient routing code.

## Java 17 Migration Notes

This project has been updated to use Java 17 (previously Java 11):
- All modules now use Java 17 toolchain and target compatibility
- Kotlin version: 2.0.21 with JVM target 17
- Android Gradle Plugin: 8.11.0 with proper namespace configurations

### Known Issues
- KSP compatibility: Sample projects using KSP may have issues with current Kotlin/KSP versions
- Ktlint compatibility: Current ktlint plugin version (3.8.0) has compatibility issues with newer Kotlin versions
- Processor tests: Some tests in deeplinkdispatch-processor module may fail due to version compatibility

## Build and Development Commands

```bash
# Build entire project (recommended - works with Java 17)
./gradlew assemble -x :sample:assemble -x :sample-benchmark:assemble -x lintKotlin -x lintKotlinMain -x lintKotlinTest

# Build core libraries only
./gradlew :deeplinkdispatch:assemble :deeplinkdispatch-base:assemble :deeplinkdispatch-processor:assemble

# Run core library tests (working)
./gradlew :deeplinkdispatch:test :deeplinkdispatch-base:test -x lintKotlin -x lintKotlinMain -x lintKotlinTest

# Run all tests (may have compatibility issues)
./gradlew test

# Run specific module tests
./gradlew :deeplinkdispatch:test
./gradlew :deeplinkdispatch-processor:test

# Run linting (has compatibility issues with current versions)
./gradlew checkstyle
./gradlew kotlinter

# Run benchmarks
./gradlew sample-benchmark:connectedCheck

# Build specific module
./gradlew :deeplinkdispatch:build

# Run sample app
./gradlew :sample:installDebug
```

## Key Architecture Components

### Module Structure
- **deeplinkdispatch-base**: Core data structures (`BaseRegistry`, `UrlTree`, `DeepLinkEntry`)
- **deeplinkdispatch**: Main Android library with `BaseDeepLinkDelegate` and dispatch logic
- **deeplinkdispatch-processor**: Annotation processor supporting KSP, KAPT, and Java annotation processing via XProcessor

### Annotation Processing Flow
1. `@DeepLink` annotations are processed at compile time
2. Processor generates registry classes containing URL matching trees
3. `BaseDeepLinkDelegate` uses generated registries to dispatch deep links at runtime
4. URL matching uses an efficient tree structure for O(n) performance

### Key Patterns
- **URL Templates**: `example://host/path/{param}` with placeholders
- **Query Parameters**: Automatic extraction and type conversion
- **DeepLinkHandler**: Type-safe handlers with data classes for parameters
- **Type Converters**: Extensible system for custom type conversion

### Testing Approach
- Unit tests use Robolectric for Android components
- Processor tests use Kotlin Compile Testing
- MockK for Kotlin mocking, standard mocks for Java
- Benchmark tests measure URL matching performance

## Development Guidelines

### When modifying the processor:
- Test with all three backends: KSP, KAPT, and Java annotation processing
- Use XProcessor APIs for cross-platform compatibility
- Run `:deeplinkdispatch-processor:test` to verify changes

### When modifying URL matching:
- Update both `UrlTree` in base module and processor generation logic
- Run benchmark tests to ensure performance isn't degraded
- Test with various URL patterns including placeholders and query parameters

### When adding new features:
- Add corresponding annotations in the base module
- Update processor to handle new annotations
- Add tests in both processor and runtime modules
- Update sample app to demonstrate usage
