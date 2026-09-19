import java.awt.Color;

public enum Prioridad {

    URGENTE(new Color(231, 76, 60)),
    ALTA(new Color(243, 156, 18)),
    NORMAL(new Color(241, 214, 64)),
    BAJA(new Color(88, 190, 110));

    private final Color color;

    Prioridad(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
