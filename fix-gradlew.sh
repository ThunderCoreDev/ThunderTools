#!/bin/bash
# Script para corregir finales de línea de gradlew

echo "Corrigiendo finales de línea de gradlew..."

# Eliminar caracteres \r (retorno de carro de Windows)
sed -i 's/\r$//' gradlew

# Verificar que la primera línea sea correcta
echo "Primera línea de gradlew:"
head -n1 gradlew | cat -A

# Hacer ejecutable
chmod +x gradlew

echo "¡Listo! gradlew corregido."