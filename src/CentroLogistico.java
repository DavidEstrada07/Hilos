public class CentroLogistico {

    public static final String[] ZONAS = {
            "Barcelona Centro", "Eixample", "Gràcia", "Sants", "Sant Martí", "Poblenou", "Badalona", "L'Hospitalet"
    };

    private final ZonaLogistica recepcion = new ZonaLogistica("Recepción", 10);
    private final ZonaLogistica almacen = new ZonaLogistica("Almacén", 20);

    private final ControlSimulacion control = new ControlSimulacion();
    private final Registro registro = new Registro();
    private final Estadisticas estadisticas = new Estadisticas(4);

    private final ZonaLogistica clasificacion = new ZonaLogistica("Clasificación", 10);
    private final ZonaLogistica empaquetado = new ZonaLogistica("Empaquetado", 8);
    private final ZonaLogistica expedicion = new ZonaLogistica("Expedición", 15);

    private int contadorPaquetes;

    public synchronized String siguienteCodigo() {
        contadorPaquetes++;
        return String.format("PKG-%04d", contadorPaquetes);
    }

    public static int rutaPara(String zona) {
        switch (zona) {
            case "Barcelona Centro":
            case "Eixample":
                return 1;
            case "Gràcia":
            case "Sants":
                return 2;
            case "Sant Martí":
            case "Poblenou":
                return 3;
            default:
                return 4;
        }
    }

    public ZonaLogistica getRecepcion() {
        return recepcion;
    }

    public ZonaLogistica getAlmacen() {
        return almacen;
    }

    public ControlSimulacion getControl() {
        return control;
    }

    public Registro getRegistro() {
        return registro;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public ZonaLogistica getClasificacion() {
        return clasificacion;
    }

    public ZonaLogistica getEmpaquetado() {
        return empaquetado;
    }

    public ZonaLogistica getExpedicion() {
        return expedicion;
    }
}