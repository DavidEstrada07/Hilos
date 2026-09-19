import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
        UIManager.put("Button.disabledText", new Color(225, 225, 225));
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}