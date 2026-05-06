package gui;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 218, 228), 1, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));
        center.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("DTU Chemistry Exam Toolkit");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 25, 50));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("All calculations you need, for the exam.");
        sub.setFont(new Font("SansSerif", Font.ITALIC, 15));
        sub.setForeground(new Color(100, 105, 120));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(10));
        center.add(title);
        center.add(Box.createVerticalStrut(8));
        center.add(sub);
        center.add(Box.createVerticalStrut(30));
        center.add(divider());
        center.add(Box.createVerticalStrut(20));

        String[][] features = {
            {"1  Equation Balancer",    "Balance any equation via Gaussian elimination."},
            {"2  pH / Acid-Base",       "Strong/weak acids & bases, buffers, neutralisation."},
            {"3  Redox",                "Oxidation states, half-reaction balancing (acid/base)."},
            {"4  Thermodynamics",       "ΔG = ΔH − TΔS,  ΔG° ↔ K,  Hess's law."},
            {"5  Kinetics",             "Arrhenius, integrated rate laws, half-life."},
            {"6  Equilibrium",          "ICE tables, Kc, Kp, Ksp, reaction quotient Q."},
            {"7  Stoichiometry",        "Molar mass, moles ↔ mass, ideal gas, yield."},
            {"8  Reference Tables",     "Constants, Ka/Kb, ΔHf°, Ksp, E°, organic classes."},
        };

        for (String[] row : features) {
            JPanel row_ = new JPanel(new BorderLayout(20, 0));
            row_.setBackground(Color.WHITE);
            row_.setMaximumSize(new Dimension(600, 28));
            row_.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel name = new JLabel(row[0]);
            name.setFont(new Font("SansSerif", Font.BOLD, 13));
            name.setForeground(new Color(37, 99, 235));
            name.setPreferredSize(new Dimension(200, 24));

            JLabel desc = new JLabel(row[1]);
            desc.setFont(new Font("SansSerif", Font.PLAIN, 13));
            desc.setForeground(new Color(60, 65, 80));

            row_.add(name, BorderLayout.WEST);
            row_.add(desc, BorderLayout.CENTER);
            center.add(row_);
            center.add(Box.createVerticalStrut(6));
        }

        center.add(Box.createVerticalStrut(20));
        center.add(divider());
        center.add(Box.createVerticalStrut(16));

        JLabel tip = new JLabel("Tip: press Enter in any text field to trigger the calculation.");
        tip.setFont(new Font("SansSerif", Font.ITALIC, 12));
        tip.setForeground(new Color(130, 130, 140));
        tip.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(tip);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 246, 250));
        wrapper.add(center);
        add(wrapper, BorderLayout.CENTER);
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(600, 1));
        sep.setForeground(new Color(220, 222, 230));
        return sep;
    }
}
