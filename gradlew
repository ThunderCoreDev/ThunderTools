#!/usr/bin/env sh

##############################################################################
## Gradle start up script for UN*X
##############################################################################

DIR="$( cd "$( dirname "$0" )" && pwd )"
APP_HOME="$DIR"

# Locate Java
if [ -n "$JAVA_HOME" ] ; then
    JAVA_EXEC="$JAVA_HOME/bin/java"
else
    JAVA_EXEC="java"
fi

# Run Gradle
exec "$JAVA_EXEC" -Xmx64m -Xms64m -cp "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"