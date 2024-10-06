#!/bin/bash

echo "Building the project..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting."
    exit 1
fi

echo "Copying JAR file to project root..."
cp target/jwt-service-1.0.0.jar ./jwt-service-1.0.0.jar

echo "Starting Docker Compose..."
docker-compose up --build