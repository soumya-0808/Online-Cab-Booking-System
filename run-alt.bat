@echo off
setlocal

REM Set the current directory as the working directory
set WORKING_DIR=%~dp0
cd /d "%WORKING_DIR%"

echo ===================================================
echo        CBRAMAN SOUMYA LAUNCHER - ALTERNATIVE RUNNER
echo ===================================================
echo.

REM Check if MySQL connector exists
if not exist "mysql-connector-java-8.0.25.jar" (
    echo MySQL connector not found. Attempting to download...
    curl -o mysql-connector-java-8.0.25.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar
    if not exist "mysql-connector-java-8.0.25.jar" (
        echo Failed to download MySQL connector.
        goto :error
    )
    echo MySQL connector downloaded successfully.
)

REM Check if compiled classes exist
if not exist "target\classes\com\dal\cabby\Application.class" (
    echo Compiled classes not found. Attempting to compile...
    call mvn clean compile
    if not exist "target\classes\com\dal\cabby\Application.class" (
        echo Failed to compile application.
        goto :error
    )
    echo Application compiled successfully.
)

echo.
echo Starting CBRaman Soumya Launcher...
echo.

REM Run the application with explicit paths
java -cp "%WORKING_DIR%target\classes;%WORKING_DIR%mysql-connector-java-8.0.25.jar" com.dal.cabby.Application

if %ERRORLEVEL% NEQ 0 (
    echo Application exited with error code: %ERRORLEVEL%
    goto :error
) else (
    echo Application exited successfully.
    goto :end
)

:error
echo.
echo ===================================================
echo                     ERROR
echo ===================================================
echo Possible issues:
echo 1. MySQL/XAMPP might not be running
echo 2. Database 'cabby' might not exist
echo 3. MySQL credentials might be incorrect
echo    (Default: username=root, password=empty)
echo 4. Java or Maven might not be properly installed
echo.
echo For manual execution, try:
echo java -cp "target\classes;mysql-connector-java-8.0.25.jar" com.dal.cabby.Application
echo.

:end
echo.
echo Press any key to exit...
pause > nul
endlocal 