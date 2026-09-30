#!/bin/bash

# ========================================
# Build and Run Script for Linux/Mac
# ========================================

echo ""
echo "Building Ashok Bank Management System..."
echo ""

# Clean previous builds
mvn clean

# Compile the project
mvn compile

# Package as JAR
mvn package

# Run the application
echo ""
echo "Starting Application..."
echo ""

mvn exec:java -Dexec.mainClass="com.ashok.bank.Main"
