package gui;

import calculators.EquationBalancer;

import javax.swing.*;
import java.awt.*;

public class EquationBalancerPanel extends BaseCalcPanel {

    private final EquationBalancer balancer = new EquationBalancer();

    public EquationBalancerPanel() {
        super("Equation Balancer");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        // Hint
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        inputPanel.add(hint("Format: &nbsp; <b>H2 + O2 -> H2O</b> &nbsp; | &nbsp; " +
                "<b>Fe2O3 + CO -> Fe + CO2</b> &nbsp; | &nbsp; <b>Ca(OH)2 + HCl -> CaCl2 + H2O</b>"), g);
        g.gridwidth = 1;

        JTextField eqField = monoField();
        g.gridy = 1;
        inputPanel.add(lbl("Equation:"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(eqField, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;

        JButton btn = calcButton("Balance  ▶");
        Runnable calc = () -> {
            String eq = eqField.getText().trim();
            if (eq.isEmpty()) { output("Please enter an equation."); return; }
            output(balancer.balance(eq));
        };
        btn.addActionListener(e -> calc.run());
        eqField.addActionListener(e -> calc.run());

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        inputPanel.add(btn, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;

        // Quick-try examples
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        inputPanel.add(lbl("Quick examples:"), g);
        g.gridy = 4;

        String[] examples = {
            "H2 + O2 -> H2O",
            "CH4 + O2 -> CO2 + H2O",
            "Fe2O3 + CO -> Fe + CO2",
            "Al + HCl -> AlCl3 + H2",
            "KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2",
        };
        JPanel exRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        exRow.setBackground(CARD_BG);
        for (String ex : examples) {
            JButton exBtn = new JButton(ex);
            exBtn.setFont(new Font("Monospaced", Font.PLAIN, 11));
            exBtn.setBackground(new Color(240, 242, 255));
            exBtn.setForeground(new Color(37, 60, 160));
            exBtn.setBorderPainted(false);
            exBtn.setFocusPainted(false);
            exBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            exBtn.addActionListener(e -> {
                eqField.setText(ex);
                output(balancer.balance(ex));
            });
            exRow.add(exBtn);
        }
        inputPanel.add(exRow, g);
    }
}
