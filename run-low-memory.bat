@echo off
echo ===================================================
echo       CBRAMAN SOUMYA LAUNCHER - LOW MEMORY RUNNER
echo ===================================================
echo.
echo.
echo Starting CBRaman Soumya Launcher...
echo.

REM Check if MySQL connector exists
if not exist mysql-connector-java-8.0.25.jar (
    echo MySQL connector not found. Downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar' -OutFile 'mysql-connector-java-8.0.25.jar'"
    if not exist mysql-connector-java-8.0.25.jar (
        echo Failed to download MySQL connector. Please download it manually.
        goto error
    )
)

REM Check if classes are compiled
if not exist target\classes\com\dal\cabby\Application.class (
    echo Compiled classes not found. Compiling...
    call mvn compile
    if not exist target\classes\com\dal\cabby\Application.class (
        echo Failed to compile. Please compile manually using 'mvn compile'.
        goto error
    )
)

REM Run the application with reduced memory
java -Xmx256m -cp "target\classes;mysql-connector-java-8.0.25.jar" com.dal.cabby.Application

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code: %ERRORLEVEL%
    echo.
    echo Possible issues:
    echo 1. MySQL/XAMPP might not be running
    echo 2. Database 'cabby' might not exist or not be properly set up
    echo 3. MySQL credentials might be incorrect (check DBHelper.java
    goto end
)

echo Application exited successfully.
goto end

:error
echo Error occurred during setup.

:end
echo.
pause 