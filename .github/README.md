# GitHub Configuration

This directory contains GitHub-specific configuration files for the Android Fourteeners project.

## Workflows

### CodeQL Analysis (`workflows/codeql.yml`)
- **Purpose**: Security and quality analysis for Java/Kotlin code
- **Trigger**: Push/PR to main branches, weekly schedule
- **Key Features**:
  - Sets up JDK 17 for Android compatibility
  - Builds the project before analysis (required for compiled languages)
  - Analyzes Java/Kotlin code for security vulnerabilities

### Android CI (`workflows/android-ci.yml`)
- **Purpose**: Continuous integration for Android app
- **Trigger**: Push/PR to main branches
- **Jobs**:
  - **Build and Test**: Runs on Ubuntu, includes lint, unit tests, and APK builds
  - **Instrumented Tests**: Runs on macOS with Android emulators (API 29, 31, 33)
- **Artifacts**: Uploads build reports and APKs

## Dependency Management

### Dependabot (`dependabot.yml`)
- **Gradle Dependencies**: Weekly updates on Mondays
- **GitHub Actions**: Monthly updates
- **Target Branch**: `develop` for testing before merging to main
- **Auto-labels**: Adds appropriate labels for easy filtering

## Contribution Management

### Code Owners (`CODEOWNERS`)
- Automatically requests reviews from designated owners
- Covers all major areas: docs, app code, build config, scripts

### Pull Request Template (`pull_request_template.md`)
- Standardized PR format
- Includes testing checklist
- Mountain-themed optional section for fun

### Issue Templates
- **Bug Report** (`ISSUE_TEMPLATE/bug_report.md`): Structured bug reporting with device info
- **Feature Request** (`ISSUE_TEMPLATE/feature_request.md`): Feature proposals with use cases

## Setup Instructions

All workflows should run automatically once pushed to GitHub. No additional setup required except:

1. Ensure repository has appropriate permissions for workflows
2. Enable Dependabot in repository settings if not auto-enabled
3. Configure branch protection rules as desired

## Workflow Status Badges

Add these badges to your main README if desired:

```markdown
[![CodeQL](https://github.com/chris-piekarski/android-fourteeners/actions/workflows/codeql.yml/badge.svg)](https://github.com/chris-piekarski/android-fourteeners/actions/workflows/codeql.yml)
[![Android CI](https://github.com/chris-piekarski/android-fourteeners/actions/workflows/android-ci.yml/badge.svg)](https://github.com/chris-piekarski/android-fourteeners/actions/workflows/android-ci.yml)
```

## Action Versions

This configuration uses the latest stable versions of GitHub Actions:
- `actions/checkout@v4`
- `actions/setup-java@v4`
- `actions/upload-artifact@v4`
- `actions/cache@v4`
- `gradle/wrapper-validation-action@v2`
- `github/codeql-action/init@v3`
- `github/codeql-action/analyze@v3`
- `reactivecircus/android-emulator-runner@v2`
- `android-actions/setup-android@v3`

## Troubleshooting

### CodeQL Build Failures
- Ensure JDK 17 is being used (required for Android Gradle Plugin)
- Check that `./gradlew build` completes successfully locally
- Verify gradle wrapper is committed and executable

### Android CI Test Failures
- Emulator tests require macOS runners (hardware acceleration)
- API level compatibility with your minSdk/targetSdk
- Timeout issues may require increasing the timeout value
- Emulator optimizations added to reduce flakiness

### Dependabot Issues
- Ensure `develop` branch exists if configured as target
- Check for conflicting dependencies that can't auto-merge
- Review security alerts separately from version updates

### Upload Artifact Deprecation
- We use `upload-artifact@v4` to avoid deprecation warnings
- Note that v4 has breaking changes from v3 (artifacts don't persist between workflow runs)