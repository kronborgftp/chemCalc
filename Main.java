import gui.ChemApp;
import ui.MenuSystem;

public class Main {
    public static void main(String[] args) {
        boolean cli = args.length > 0 && args[0].equals("--cli");
        if (cli) {
            new MenuSystem().start();
        } else {
            try {
                javax.swing.UIManager.setLookAndFeel(
                        "javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
            javax.swing.SwingUtilities.invokeLater(ChemApp::new);
        }
    }
}
