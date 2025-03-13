#!/bin/bash

mvn clean package
mvn clean install
mvn clean compile
echo "Starting the application"

java -jar target/cabby-1.0-jar-with-dependencies.jar
