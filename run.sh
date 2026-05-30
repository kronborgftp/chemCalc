#!/bin/bash
# Compile and run the Chemistry Exam Toolkit
cd "$(dirname "$0")"
echo "Compiling..."
find . -name "*.java" | xargs javac -d out
if [ $? -eq 0 ]; then
    echo "Done. Starting..."
    java -cp out Main
else
    echo "Compilation failed."
fi
