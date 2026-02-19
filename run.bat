@echo off
echo ========================================
echo JavaFX Admin Panel Application
echo ========================================
echo.
echo Starting application...
echo.

mvn clean javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo Error: Failed to start application
    echo ========================================
    echo.
    echo Please make sure:
    echo 1. Maven is installed and in PATH
    echo 2. Java JDK 11 or higher is installed
    echo 3. You are in the correct directory
    echo.
    pause
)
