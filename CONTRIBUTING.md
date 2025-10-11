# Contributing to Android Fourteeners

First off, thank you for considering contributing to Android Fourteeners! It's people like you who make this app a great tool for the Colorado hiking community.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Process](#development-process)
- [Style Guidelines](#style-guidelines)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to the project maintainers.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 1.8 or higher
- Android SDK with minimum API 21
- Kotlin 1.8.22 or newer
- Git

### Setting up your development environment

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/android-fourteeners.git
   cd android-fourteeners
   ```
3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/original-owner/android-fourteeners.git
   ```
4. Open the project in Android Studio
5. Sync project with Gradle files
6. Run the app on an emulator or physical device

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples**
- **Describe the behavior you observed and what you expected**
- **Include screenshots if possible**
- **Include your device information** (Android version, device model, app version)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

- **Use a clear and descriptive title**
- **Provide a detailed description of the suggested enhancement**
- **Provide specific examples to demonstrate the feature**
- **Describe the current behavior and expected behavior**
- **Explain why this enhancement would be useful**

### Your First Code Contribution

Unsure where to begin? You can start by looking through these issues:

- Issues labeled `good first issue` - should only require a few lines of code
- Issues labeled `help wanted` - more involved issues
- Issues labeled `documentation` - improve or write documentation

### Pull Requests

1. Follow the [style guidelines](#style-guidelines)
2. Include appropriate test cases
3. Follow the [commit guidelines](#commit-guidelines)
4. Update documentation as needed
5. End all files with a newline

## Development Process

### Branching Strategy

- `master` - Production-ready code
- `develop` - Development branch
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `hotfix/*` - Urgent fixes for production

### Creating a Feature Branch

```bash
git checkout develop
git pull upstream develop
git checkout -b feature/your-feature-name
```

## Style Guidelines

### Kotlin Style Guide

We follow the [Official Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with these additions:

```kotlin
// Class naming - use PascalCase
class MountainActivity : AppCompatActivity()

// Function naming - use camelCase
fun calculateElevationGain(): Int

// Constants - use UPPER_SNAKE_CASE
companion object {
    const val MAX_ELEVATION = 14440
}

// Properties - use camelCase
private val mountainList: List<Mountain>

// Use explicit types for public APIs
fun getMountain(name: String): Mountain?

// Prefer immutability
val mountain = Mountain() // Use val instead of var when possible
```

### XML Style Guide

- Use `snake_case` for IDs
- Use meaningful prefixes (e.g., `button_submit`, `text_mountain_name`)
- Organize attributes: ID, layout, padding/margin, other attributes

```xml
<TextView
    android:id="@+id/text_elevation"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="16dp"
    android:text="14,433 ft" />
```

### Resource Naming

- Layouts: `activity_`, `fragment_`, `item_`, `dialog_`
- Drawables: `ic_` for icons, `bg_` for backgrounds, `img_` for images
- Strings: Use descriptive names with underscores
- Colors: Name by purpose, not color (e.g., `primary_text` not `black_text`)

## Commit Guidelines

We follow conventional commits for clear history:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only changes
- `style`: Code style changes (formatting, missing semicolons, etc)
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `perf`: Performance improvements
- `test`: Adding missing tests
- `build`: Changes to build system or dependencies
- `ci`: Changes to CI configuration files
- `chore`: Other changes that don't modify src or test files

### Examples

```
feat(location): add nearest mountain detection

Implement algorithm to detect the 10 nearest fourteeners
based on current GPS location. Uses Haversine formula
for distance calculation.

Closes #123
```

```
fix(database): prevent SQL injection in summit queries

Use parameterized queries for all user input in
RegisterEntry class.

Fixes #456
```

## Pull Request Process

1. **Update your branch**:
   ```bash
   git fetch upstream
   git rebase upstream/develop
   ```

2. **Run tests**:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

3. **Check code style**:
   ```bash
   ./gradlew ktlintCheck
   ```

4. **Update documentation** if you're changing functionality

5. **Fill out the PR template** completely

6. **Request review** from maintainers

### PR Title Format

Use the same convention as commits:
- `feat: add summit photo sharing`
- `fix: correct elevation calculation`
- `docs: update setup instructions`

### PR Description Template

```markdown
## Description
Brief description of what this PR does

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Instrumented tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots here

## Checklist
- [ ] My code follows the project style guidelines
- [ ] I have performed a self-review
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing unit tests pass locally
- [ ] I have updated documentation
```

## Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### Writing Tests

- Write unit tests for business logic
- Write instrumented tests for Android-specific code
- Aim for at least 70% code coverage
- Use descriptive test names

```kotlin
@Test
fun `calculateElevationGain returns correct value for valid input`() {
    // Given
    val startElevation = 10000
    val endElevation = 14000

    // When
    val gain = calculateElevationGain(startElevation, endElevation)

    // Then
    assertEquals(4000, gain)
}
```

## Recognition

Contributors who submit accepted PRs will be added to our [Contributors](README.md#contributors) section in the README.

## Questions?

Feel free to open an issue with the `question` label or reach out to the maintainers directly.

Thank you for contributing to Android Fourteeners! 🏔️