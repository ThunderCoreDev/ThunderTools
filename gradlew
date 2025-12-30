
#!/usr/bin/env bash

# Simple Gradle wrapper for ThunderTools
GRADLE_WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
    echo "Error: Gradle wrapper JAR not found at $GRADLE_WRAPPER_JAR"
    echo "Make sure you have gradle/wrapper/gradle-wrapper.properties"
    exit 1
fi

exec java -jar "$GRADLE_WRAPPER_JAR" "$@"
