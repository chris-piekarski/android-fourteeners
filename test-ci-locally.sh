#!/bin/bash

# Local CI/CD Test Script
# This simulates the GitHub Actions CI pipeline locally (as much as possible without Android SDK)

echo "========================================="
echo "  Local CI/CD Pre-flight Check"
echo "========================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track issues found
ISSUES_FOUND=0

echo "🔍 Checking environment..."

# Check if Gradle wrapper exists and is executable
if [ -f "./gradlew" ]; then
    echo -e "${GREEN}✓${NC} Gradle wrapper found"
    if [ -x "./gradlew" ]; then
        echo -e "${GREEN}✓${NC} Gradle wrapper is executable"
    else
        echo -e "${YELLOW}⚠${NC} Gradle wrapper is not executable. CI will run: chmod +x gradlew"
    fi
else
    echo -e "${RED}✗${NC} Gradle wrapper not found!"
    ((ISSUES_FOUND++))
fi

# Check for required files
echo ""
echo "📁 Checking required files..."

FILES_TO_CHECK=(
    "app/build.gradle"
    "app/src/main/AndroidManifest.xml"
    "gradle/wrapper/gradle-wrapper.jar"
    "gradle/wrapper/gradle-wrapper.properties"
)

for file in "${FILES_TO_CHECK[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $file exists"
    else
        echo -e "${RED}✗${NC} $file missing!"
        ((ISSUES_FOUND++))
    fi
done

# Check for layout files referenced in activities
echo ""
echo "🎨 Checking layout resources..."

LAYOUTS_TO_CHECK=(
    "app/src/main/res/layout/activity_home.xml"
    "app/src/main/res/layout/activity_add_bag.xml"
    "app/src/main/res/layout/activity_register.xml"
    "app/src/main/res/layout/activity_hike.xml"
    "app/src/main/res/layout/activity_license.xml"
    "app/src/main/res/layout/activity_leave_no_trace.xml"
    "app/src/main/res/layout/history_list_view.xml"
    "app/src/main/res/layout/hike_list_view.xml"
)

for layout in "${LAYOUTS_TO_CHECK[@]}"; do
    if [ -f "$layout" ]; then
        echo -e "${GREEN}✓${NC} $(basename $layout) exists"
    else
        echo -e "${RED}✗${NC} $(basename $layout) missing!"
        ((ISSUES_FOUND++))
    fi
done

# Check for potential Kotlin compilation issues
echo ""
echo "📝 Checking for common Kotlin issues..."

# Check for const val with string interpolation (should be val)
# Look for const val with actual string templates using ${
if grep -r "const val.*\${" app/src/main/kotlin 2>/dev/null | grep -v "//"; then
    echo -e "${YELLOW}⚠${NC} Found const val with string interpolation (should be val)"
    ((ISSUES_FOUND++))
else
    echo -e "${GREEN}✓${NC} No const val issues found"
fi

# Check for deprecated test imports
echo ""
echo "🧪 Checking test configuration..."

if grep -r "android.test.AndroidTestCase" app/src/androidTest 2>/dev/null; then
    echo -e "${RED}✗${NC} Found deprecated AndroidTestCase usage"
    ((ISSUES_FOUND++))
else
    echo -e "${GREEN}✓${NC} No deprecated AndroidTestCase found"
fi

if grep -r "junit.framework.Assert" app/src/androidTest 2>/dev/null; then
    echo -e "${RED}✗${NC} Found deprecated junit.framework.Assert usage"
    ((ISSUES_FOUND++))
else
    echo -e "${GREEN}✓${NC} No deprecated JUnit 3 assertions found"
fi

# Check AndroidManifest for required Android 12+ attributes
echo ""
echo "📱 Checking AndroidManifest for Android 12+ compatibility..."

# Count activities with android:exported
ACTIVITY_COUNT=$(grep -c "<activity" app/src/main/AndroidManifest.xml)
EXPORTED_COUNT=$(grep -c "android:exported" app/src/main/AndroidManifest.xml)

if [ "$ACTIVITY_COUNT" -eq "$EXPORTED_COUNT" ]; then
    echo -e "${GREEN}✓${NC} All activities have android:exported attribute"
else
    echo -e "${RED}✗${NC} Missing android:exported on some activities ($EXPORTED_COUNT/$ACTIVITY_COUNT)"
    ((ISSUES_FOUND++))
fi

# Check for namespace in build.gradle
if grep -q "namespace " app/build.gradle; then
    echo -e "${GREEN}✓${NC} Namespace defined in build.gradle"
else
    echo -e "${RED}✗${NC} Namespace not defined in build.gradle"
    ((ISSUES_FOUND++))
fi

# Check if package attribute removed from manifest
if grep -q 'package=' app/src/main/AndroidManifest.xml; then
    echo -e "${YELLOW}⚠${NC} Package attribute still in AndroidManifest.xml (deprecated)"
fi

# Try to run Gradle tasks if possible
echo ""
echo "🔨 Attempting Gradle tasks..."

if [ -f "./gradlew" ]; then
    # Try to run basic Gradle task
    if ./gradlew tasks --no-daemon > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Gradle wrapper is functional"

        # List available tasks
        echo ""
        echo "📋 Available Gradle tasks:"
        ./gradlew tasks --no-daemon 2>/dev/null | grep -E "^[a-z]" | head -20
    else
        echo -e "${YELLOW}⚠${NC} Gradle tasks failed (likely due to missing Android SDK locally)"
        echo "   This is expected locally but will work in CI/CD with Android SDK"
    fi
fi

# Summary
echo ""
echo "========================================="
echo "  Summary"
echo "========================================="

if [ $ISSUES_FOUND -eq 0 ]; then
    echo -e "${GREEN}✅ No issues found! Your code should pass CI/CD.${NC}"
else
    echo -e "${RED}❌ Found $ISSUES_FOUND issue(s) that may cause CI/CD to fail.${NC}"
    echo "   Please fix the issues above before pushing."
fi

echo ""
echo "Note: This script cannot fully test the build without Android SDK."
echo "The actual CI/CD will:"
echo "  1. Set up JDK 17"
echo "  2. Run: ./gradlew lint"
echo "  3. Run: ./gradlew ktlintCheck"
echo "  4. Run: ./gradlew test"
echo "  5. Run: ./gradlew assembleDebug"
echo "  6. Run: ./gradlew assembleRelease"
echo "  7. Run instrumented tests on Android emulator (API 29)"

exit $ISSUES_FOUND