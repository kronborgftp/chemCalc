import gui.ChemApp;

public class Main {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        javax.swing.SwingUtilities.invokeLater(ChemApp::new);
    }
}
