#!/bin/bash

# Compile the project
echo "Compiling project..."
mvn clean package

# Run tests for each example file
echo -e "\n\n=== Running Simple Double Test ==="
java -cp target/stackphy-0.1.0.jar io.github.stackphy.FunctionDefinitionTest simple_double.stackphy "Simple double function"

echo -e "\n\n=== Fixed Normal PDF Test Ops ==="
java -cp target/stackphy-0.1.0.jar io.github.stackphy.FunctionDefinitionTest fixed_normal_pdf.stackphy "Fixed Normal PDF function"

echo -e "\n\n=== Running Standard Normal Test ==="
java -cp target/stackphy-0.1.0.jar io.github.stackphy.FunctionDefinitionTest standard_normal.stackphy "Standard Normal PDF"

echo -e "\n\nAll tests completed."
