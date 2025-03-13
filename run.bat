@echo off
title CBRaman Soumya Launcher
color 0A

echo ===================================================
echo             CBRAMAN SOUMYA LAUNCHER
echo ===================================================
echo Make sure MySQL/XAMPP is running and the cabby database is properly set up.
echo.

REM Check if MySQL connector exists
if not exist "mysql-connector-java-8.0.25.jar" (
    echo ERROR: MySQL connector JAR file not found!
    echo Downloading MySQL connector...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar' -OutFile 'mysql-connector-java-8.0.25.jar'}"
    if not exist "mysql-connector-java-8.0.25.jar" (
        echo Failed to download MySQL connector.
        echo Please download it manually from:
        echo https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar
        pause
        exit /b 1
    )
    echo MySQL connector downloaded successfully.
)

REM Check if classes directory exists
if not exist "target\classes" (
    echo ERROR: Compiled classes not found!
    echo Compiling the application...
    call mvn clean compile
    if not exist "target\classes" (
        echo Failed to compile the application.
        echo Please run 'mvn clean compile' manually.
        pause
        exit /b 1
    )
    echo Application compiled successfully.
)

echo.
echo Starting CBRaman Soumya Launcher...
echo.

REM Run the application with proper classpath
java -cp "target\classes;mysql-connector-java-8.0.25.jar" com.dal.cabby.Application

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Application exited with error code: %ERRORLEVEL%
    echo.
    echo Possible issues:
    echo 1. MySQL/XAMPP might not be running
    echo 2. Database 'cabby' might not exist or not be properly set up
    echo 3. MySQL credentials might be incorrect (check DBHelper.java)
) else (
    echo Application exited successfully.
)

echo.
echo Press any key to close this window...
pause > nul 