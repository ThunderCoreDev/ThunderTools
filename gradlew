#!/bin/bash

# Script simplificado para GitHub Actions
# Elimina todas las funciones complejas y usa gradle directamente

# Configurar variables básicas
GRADLE_HOME=${GRADLE_HOME:-""}
JAVA_HOME=${JAVA_HOME:-""}

# Buscar Java
if [ -n "$JAVA_HOME" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=$(which java)
    if [ $? -ne 0 ]; then
        echo "ERROR: No se encontró Java en el PATH"
        echo "Por favor, configura JAVA_HOME o instala Java"
        exit 1
    fi
fi

# Verificar que exista el wrapper JAR
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "ERROR: No se encontró gradle/wrapper/gradle-wrapper.jar"
    echo "Ejecuta: curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.5.0/gradle/wrapper/gradle-wrapper.jar"
    exit 1
fi

# Ejecutar Gradle con los parámetros
"$JAVA" -jar gradle/wrapper/gradle-wrapper.jar "$@"