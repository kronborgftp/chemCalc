package gui;

import javax.swing.*;
import java.awt.*;

public class ChemApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    public ChemApp() {
        super("DTU Chemistry Exam Toolkit  v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ── Title bar ─────────────────────────────────────────────────────────
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(18, 32, 68));
        titleBar.setPreferredSize(new Dimension(0, 44));
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        JLabel title = new JLabel("DTU Chemistry Exam Toolkit");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel version = new JLabel("v1.0");
        version.setForeground(new Color(150, 170, 220));
        version.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(version, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // ── Sidebar ───────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // ── Content panels ────────────────────────────────────────────────────
        content.setBackground(new Color(245, 246, 250));
        content.add(new HomePanel(), "home");
        content.add(new EquationBalancerPanel(), "eq");
        content.add(new PHPanel(), "ph");
        content.add(new RedoxPanel(), "redox");
        content.add(new ElectrochemistryPanel(), "electro");
        content.add(new ThermodynamicsPanel(), "thermo");
        content.add(new KineticsPanel(), "kinetics");
        content.add(new EquilibriumPanel(), "equil");
        content.add(new StoichiometryPanel(), "stoich");
        content.add(new VESPRPanel(), "vsepr");
        content.add(new ReferencePanel(), "ref");
        add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(24, 28, 42));
        sidebar.setPreferredSize(new Dimension(188, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

        String[][] items = {
                { "Home", "home" },
                { "Equation Balancer", "eq" },
                { "pH / Acid-Base", "ph" },
                { "Redox", "redox" },
                { "Electrochemistry", "electro" },
                { "Thermodynamics", "thermo" },
                { "Kinetics", "kinetics" },
                { "Equilibrium", "equil" },
                { "Stoichiometry", "stoich" },
                { "VSEPR / Geometry", "vsepr" },
                { "Reference Tables", "ref" }
        };

        for (String[] item : items) {
            sidebar.add(navBtn(item[0], item[1]));
            sidebar.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton navBtn(String label, String card) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(172, 36));
        btn.setPreferredSize(new Dimension(172, 36));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(new Color(36, 41, 60));
        btn.setForeground(new Color(210, 215, 230));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        btn.addActionListener(e -> cards.show(content, card));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(37, 99, 200));
                btn.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(36, 41, 60));
                btn.setForeground(new Color(210, 215, 230));
            }
        });
        return btn;
    }
}
