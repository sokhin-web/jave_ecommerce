#!/bin/bash

# Compile all Java files
echo "Compiling Java files..."
javac -d bin src/main/java/com/adminpanel/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running the application..."
    java -cp bin com.adminpanel.DashboardApp
else
    echo "Compilation failed!"
    exit 1
fi
