#!/usr/bin/env bash
# ThunderTools Gradle Wrapper - Linux/Mac
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$BASE_DIR/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
    echo "ERROR: gradle-wrapper.jar not found at $WRAPPER_JAR"
    exit 1
fi
exec java -jar "$WRAPPER_JAR" "$@"
