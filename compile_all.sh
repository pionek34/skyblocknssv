#!/bin/bash

# Kompilacja całego projektu
cd /mnt/user-data/outputs/skyblocknssv
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "✓ Kompilacja zakończona sukcesem!"
    echo "✓ JAR: target/SkyblocknNSSV-1.0.0.jar"
else
    echo "✗ Błąd kompilacji!"
    exit 1
fi
