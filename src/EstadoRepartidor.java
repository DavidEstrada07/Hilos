import java.awt.Color;

public enum EstadoRepartidor {

    DISPONIBLE(new Color(40, 150, 70)),
    CARGANDO(new Color(40, 110, 200)),
    EN_RUTA(new Color(220, 120, 0)),
    ENTREGANDO(new Color(140, 60, 170)),
    REGRESANDO(new Color(110, 110, 110)),
    FUERA_DE_SERVICIO(new Color(200, 40, 40));

    private final Color color;

    EstadoRepartidor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getTexto() {
        return name().replace('_', ' ');
    }
}
