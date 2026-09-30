@echo off
REM ========================================
REM Build and Run Script for Windows
REM ========================================

echo.
echo Building Ashok Bank Management System...
echo.

REM Clean previous builds
mvn clean

REM Compile the project
mvn compile

REM Package as JAR
mvn package

REM Run the application
echo.
echo Starting Application...
echo.

mvn exec:java -Dexec.mainClass="com.ashok.bank.Main"

pause
