public class CentroLogistico {

    public static final String[] ZONAS = {
            "Barcelona Centro", "Eixample", "Gràcia", "Sants", "Sant Martí", "Poblenou", "Badalona", "L'Hospitalet"
    };

    private final ZonaLogistica recepcion = new ZonaLogistica("Recepción", 10);
    private final ZonaLogistica almacen = new ZonaLogistica("Almacén", 20);

    private final ControlSimulacion control = new ControlSimulacion();
    private final Registro registro = new Registro();
    private final Estadisticas estadisticas = new Estadisticas(4);

    private int contadorPaquetes;

    public synchronized String siguienteCodigo() {
        contadorPaquetes++;
        return String.format("PKG-%04d", contadorPaquetes);
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
}