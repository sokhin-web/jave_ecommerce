#!/bin/bash

echo "========================================"
echo "JavaFX Admin Panel Application"
echo "========================================"
echo ""
echo "Starting application..."
echo ""

# Use Maven to run the application
mvn clean javafx:run

if [ $? -ne 0 ]; then
    echo ""
    echo "========================================"
    echo "Error: Failed to start application"
    echo "========================================"
    echo ""
    echo "Please make sure:"
    echo "1. Maven is installed and in PATH"
    echo "2. Java JDK 11 or higher is installed"
    echo "3. You are in the correct directory"
    echo ""
    exit 1
fi
