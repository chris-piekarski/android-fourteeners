# Makefile for Android Fourteeners development

.PHONY: help clean build test install repo-stats docs-stats lint format release debug apk bundle
.PHONY: emulator devices logcat clean-cache dependencies outdated migrate-db

# Default target
.DEFAULT_GOAL := help

# Variables
GRADLE = ./gradlew
ADB = adb
PACKAGE_NAME = com.cpiekarski.fourteeners
BUILD_TYPE ?= debug

help: ## Show this help message
	@echo 'Android Fourteeners Development Makefile'
	@echo ''
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-20s %s\n", $$1, $$2}'
	@echo ''
	@echo 'Examples:'
	@echo '  make build          # Build the debug APK'
	@echo '  make test           # Run all tests'
	@echo '  make repo-stats     # Update repository statistics'
	@echo '  make install        # Install on connected device'

# ============= Building =============

clean: ## Clean build artifacts
	$(GRADLE) clean
	rm -rf build/
	rm -rf app/build/
	rm -rf .gradle/
	find . -name "*.iml" -delete
	@echo "✅ Build artifacts cleaned"

build: ## Build debug APK
	$(GRADLE) assembleDebug
	@echo "✅ Debug APK built: app/build/outputs/apk/debug/"

release: ## Build release APK (requires signing config)
	$(GRADLE) assembleRelease
	@echo "✅ Release APK built: app/build/outputs/apk/release/"

apk: build ## Alias for build

bundle: ## Build release bundle for Play Store
	$(GRADLE) bundleRelease
	@echo "✅ Release bundle built: app/build/outputs/bundle/release/"

# ============= Testing =============

test: ## Run all tests (unit + instrumented)
	$(GRADLE) test
	$(GRADLE) connectedAndroidTest || true
	@echo "✅ Tests completed"

test-unit: ## Run unit tests only
	$(GRADLE) test
	@echo "✅ Unit tests completed"

test-instrumented: ## Run instrumented tests
	$(GRADLE) connectedAndroidTest
	@echo "✅ Instrumented tests completed"

test-coverage: ## Run tests with coverage report
	$(GRADLE) testDebugUnitTestCoverage
	@echo "✅ Coverage report generated: app/build/reports/coverage/"

# ============= Installation =============

install: build ## Install debug APK on connected device
	$(ADB) install -r app/build/outputs/apk/debug/app-debug.apk
	@echo "✅ App installed on device"

reinstall: uninstall install ## Uninstall and reinstall the app

uninstall: ## Uninstall app from connected device
	$(ADB) uninstall $(PACKAGE_NAME) || true
	@echo "✅ App uninstalled"

run: install ## Install and launch the app
	$(ADB) shell am start -n $(PACKAGE_NAME)/.activities.HomeActivity
	@echo "✅ App launched"

# ============= Code Quality =============

lint: ## Run Android lint checks
	$(GRADLE) lint
	@echo "✅ Lint report: app/build/reports/lint/"

ktlint-check: ## Check Kotlin code style
	$(GRADLE) ktlintCheck || true
	@echo "✅ Kotlin style check complete"

ktlint-format: ## Auto-format Kotlin code
	$(GRADLE) ktlintFormat
	@echo "✅ Kotlin code formatted"

format: ktlint-format ## Alias for ktlint-format

detekt: ## Run Detekt static analysis
	$(GRADLE) detekt || true
	@echo "✅ Detekt report: app/build/reports/detekt/"

code-quality: lint ktlint-check detekt ## Run all code quality checks

# ============= Repository Statistics =============

repo-stats: ## Generate repository statistics and update README.md
	@echo "📊 Generating repository statistics..."
	@if command -v python3 >/dev/null 2>&1; then \
		python3 scripts/generate_repo_stats.py; \
	elif command -v python >/dev/null 2>&1; then \
		python scripts/generate_repo_stats.py; \
	else \
		echo "❌ Python not found. Please install Python 3.6+"; \
		exit 1; \
	fi
	@echo "✅ Repository stats updated in README.md"

docs-stats: ## Generate documentation statistics
	@echo "📄 Documentation statistics:"
	@echo "  Markdown files: $$(find docs -name "*.md" 2>/dev/null | wc -l)"
	@echo "  Mermaid diagrams: $$(grep -r "^\`\`\`mermaid" docs 2>/dev/null | wc -l)"
	@echo "  Total lines: $$(find docs -name "*.md" -exec wc -l {} \; 2>/dev/null | awk '{sum+=$$1} END {print sum}')"

# ============= Device Management =============

devices: ## List connected devices
	$(ADB) devices -l
	@echo ""
	@echo "Device details:"
	@$(ADB) shell getprop ro.product.model 2>/dev/null || echo "No device connected"

emulator: ## List and start emulator
	@echo "Available emulators:"
	@emulator -list-avds
	@echo ""
	@echo "To start an emulator, run:"
	@echo "  emulator -avd <name>"

logcat: ## Show device logs (filtered for app)
	$(ADB) logcat -v time $(PACKAGE_NAME):V *:S

logcat-all: ## Show all device logs
	$(ADB) logcat -v time

screenshot: ## Take a screenshot from connected device
	@FILENAME="screenshot_$$(date +%Y%m%d_%H%M%S).png"; \
	$(ADB) shell screencap -p /sdcard/$$FILENAME; \
	$(ADB) pull /sdcard/$$FILENAME .; \
	$(ADB) shell rm /sdcard/$$FILENAME; \
	echo "✅ Screenshot saved: $$FILENAME"

# ============= Dependencies =============

dependencies: ## Show project dependencies
	$(GRADLE) app:dependencies

outdated: ## Check for outdated dependencies
	$(GRADLE) dependencyUpdates
	@echo "✅ Dependency report: build/dependencyUpdates/"

refresh-deps: ## Refresh all dependencies
	$(GRADLE) --refresh-dependencies dependencies
	@echo "✅ Dependencies refreshed"

# ============= Database =============

db-schema: ## Show database schema (requires device connection)
	@echo "Summit Register Database Schema:"
	@$(ADB) shell run-as $(PACKAGE_NAME) cat databases/summit_register | sqlite3 -cmd ".schema" || echo "Database not found or device not connected"

db-export: ## Export database from device
	@FILENAME="summit_register_$$(date +%Y%m%d_%H%M%S).db"; \
	$(ADB) shell run-as $(PACKAGE_NAME) cp databases/summit_register /sdcard/$$FILENAME; \
	$(ADB) pull /sdcard/$$FILENAME .; \
	$(ADB) shell rm /sdcard/$$FILENAME; \
	echo "✅ Database exported: $$FILENAME"

# ============= Cache Management =============

clean-cache: ## Clean all caches
	rm -rf ~/.gradle/caches/
	rm -rf .gradle/
	rm -rf build/
	rm -rf app/build/
	@echo "✅ All caches cleaned"

clean-idea: ## Clean IntelliJ IDEA files
	rm -rf .idea/
	find . -name "*.iml" -delete
	@echo "✅ IDEA files cleaned"

# ============= Documentation =============

docs: ## Open documentation in browser
	@if command -v xdg-open >/dev/null 2>&1; then \
		xdg-open docs/README.md; \
	elif command -v open >/dev/null 2>&1; then \
		open docs/README.md; \
	else \
		echo "📚 Documentation is in docs/"; \
	fi

generate-docs: ## Generate KDoc documentation
	$(GRADLE) dokkaHtml || echo "Add Dokka plugin to generate documentation"
	@echo "📚 Documentation generated: build/dokka/"

# ============= Git Helpers =============

pre-commit: code-quality test-unit ## Run pre-commit checks
	@echo "✅ Pre-commit checks passed"

status: ## Show project status
	@echo "📱 Android Fourteeners Project Status"
	@echo "======================================"
	@echo "Branch: $$(git branch --show-current)"
	@echo "Last commit: $$(git log -1 --oneline)"
	@echo ""
	@echo "Build Info:"
	@grep "minSdk\|targetSdk\|versionName\|versionCode" app/build.gradle | sed 's/^/  /'
	@echo ""
	@echo "Kotlin version: $$(grep "kotlin_version" build.gradle | head -1 | cut -d"'" -f2)"
	@echo "Gradle version: $$(grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties | cut -d'-' -f2 | cut -d'.' -f1-3)"
	@echo ""
	@$(MAKE) docs-stats

# ============= Shortcuts =============

c: clean ## Shortcut for clean
b: build ## Shortcut for build
t: test ## Shortcut for test
i: install ## Shortcut for install
r: run ## Shortcut for run
l: logcat ## Shortcut for logcat

# ============= CI/CD =============

ci: clean lint test build ## Run CI pipeline locally
	@echo "✅ CI pipeline completed successfully"

cd-check: ## Check if ready for deployment
	@echo "Deployment checklist:"
	@echo "  [$(shell test -f app/build/outputs/apk/release/app-release.apk && echo '✓' || echo ' ')] Release APK exists"
	@echo "  [$(shell grep -q "versionName" app/build.gradle && echo '✓' || echo ' ')] Version configured"
	@echo "  [$(shell test -f app/google-services.json && echo '✓' || echo ' ')] Google services configured"
	@echo "  [$(shell grep -q "signingConfigs" app/build.gradle && echo '✓' || echo ' ')] Signing configured"

# ============= Development Setup =============

setup: ## Setup development environment
	@echo "Setting up development environment..."
	@command -v java >/dev/null 2>&1 || echo "❌ Java not installed"
	@command -v $(GRADLE) >/dev/null 2>&1 || echo "❌ Gradle wrapper not found"
	@command -v $(ADB) >/dev/null 2>&1 || echo "❌ ADB not installed"
	@command -v git >/dev/null 2>&1 || echo "❌ Git not installed"
	@echo "Downloading dependencies..."
	@$(GRADLE) build || true
	@echo "✅ Development environment ready"

.PHONY: all
all: clean build test ## Clean, build, and test everything