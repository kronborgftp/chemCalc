#!/bin/bash
# Compile and run the Chemistry Exam Toolkit
cd "$(dirname "$0")"
echo "Compiling..."
javac chemistry/Element.java chemistry/PeriodicTable.java chemistry/FormulaParser.java \
      calculators/Calculator.java \
      calculators/EquationBalancer.java \
      calculators/PHCalculator.java \
      calculators/RedoxCalculator.java \
      calculators/ThermodynamicsCalc.java \
      calculators/KineticsCalc.java \
      calculators/EquilibriumCalc.java \
      calculators/StoichiometryCalc.java \
      ui/MenuSystem.java \
      Main.java
if [ $? -eq 0 ]; then
    echo "Compiled successfully. Starting..."
    java Main
else
    echo "Compilation failed."
fi
