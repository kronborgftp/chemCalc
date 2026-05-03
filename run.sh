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
      gui/BaseCalcPanel.java \
      gui/HomePanel.java \
      gui/EquationBalancerPanel.java \
      gui/PHPanel.java \
      gui/RedoxPanel.java \
      gui/ThermodynamicsPanel.java \
      gui/KineticsPanel.java \
      gui/EquilibriumPanel.java \
      gui/StoichiometryPanel.java \
      gui/ReferencePanel.java \
      gui/ChemApp.java \
      Main.java
if [ $? -eq 0 ]; then
    echo "Compiled successfully. Starting GUI..."
    echo "(Use 'java Main --cli' for terminal mode)"
    java Main
else
    echo "Compilation failed."
fi
