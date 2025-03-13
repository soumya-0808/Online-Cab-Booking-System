#!/bin/bash

echo "====================================================="
echo "             CABBY APPLICATION LAUNCHER               "
echo "====================================================="
echo "Make sure MySQL is running and the cabby database is properly set up."
echo ""

# Check if MySQL connector exists
if [ ! -f "mysql-connector-java-8.0.25.jar" ]; then
    echo "MySQL connector not found. Downloading..."
    curl -o mysql-connector-java-8.0.25.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar
    if [ ! -f "mysql-connector-java-8.0.25.jar" ]; then
        echo "Failed to download MySQL connector."
        exit 1
    fi
    echo "MySQL connector downloaded successfully."
fi

# Check if compiled classes exist
if [ ! -d "target/classes" ]; then
    echo "Compiled classes not found. Compiling..."
    mvn clean compile
    if [ ! -d "target/classes" ]; then
        echo "Failed to compile application."
        exit 1
    fi
    echo "Application compiled successfully."
fi

echo ""
echo "Starting Cabby Application..."
echo ""

# Run the application
java -cp "target/classes:mysql-connector-java-8.0.25.jar" com.dal.cabby.Application

# Check exit status
if [ $? -ne 0 ]; then
    echo ""
    echo "Application exited with an error."
    echo ""
    echo "Possible issues:"
    echo "1. MySQL might not be running"
    echo "2. Database 'cabby' might not exist"
    echo "3. MySQL credentials might be incorrect (check DBHelper.java)"
    echo "4. Java or Maven might not be properly installed"
else
    echo ""
    echo "Application exited successfully."
fi

echo ""
echo "Press Enter to exit..."
read 