# Security Policy

## Supported Versions

Currently supporting security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take security seriously in the Android Fourteeners app, as user location data and summit records are sensitive information.

### How to Report

If you discover a security vulnerability, please follow these steps:

1. **DO NOT** create a public GitHub issue for the vulnerability
2. Create a security advisory at: [Create a security advisory](https://github.com/your-repo/android-fourteeners/security/advisories/new)
3. Include the following information:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if available)

### What to Expect

- **Initial Response**: Within 48 hours, we'll acknowledge receipt of your report
- **Assessment**: Within 7 days, we'll provide an initial assessment and expected timeline
- **Updates**: We'll keep you informed of progress at least every 2 weeks
- **Resolution**: Once fixed, we'll notify you and coordinate disclosure

### Security Best Practices for Contributors

When contributing to this project, please ensure:

- **Location Data**: Never log or expose precise GPS coordinates in debug builds
- **Database Security**: Use parameterized queries (already implemented in Kotlin migration)
- **Permissions**: Request only necessary permissions and check them at runtime
- **Storage**: Summit photos and personal data should be stored securely
- **Dependencies**: Keep all dependencies up to date

### Scope

Security vulnerabilities we're particularly concerned about:

- Location data exposure or tracking
- SQL injection in summit records database
- Unauthorized access to user's summit history
- Insecure storage of photos or personal information
- Permission escalation vulnerabilities

### Recognition

We appreciate responsible disclosure and will acknowledge security researchers who help improve our app's security in our release notes (with your permission).

## Security Features

The app currently implements:

- Runtime permission checks for location services
- Parameterized SQL queries to prevent injection
- Local-only data storage (no cloud sync)
- Minimal permission requirements

## Contact

For urgent security matters, please tag maintainers directly in a private security advisory on GitHub.

Thank you for helping keep the Android Fourteeners app secure!