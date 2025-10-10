.PHONY: test lint build clean

GRADLE=./gradlew

build:
	$(GRADLE) assembleDebug

clean:
	$(GRADLE) clean

# Run JVM unit tests (fast)
test:
	$(GRADLE) testDebugUnitTest --stacktrace

# Run Android Lint
lint:
	$(GRADLE) :app:lintDebug --stacktrace
