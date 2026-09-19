public class CentroLogistico {

    public static final String[] ZONAS = {
            "Barcelona Centro", "Eixample", "Gràcia", "Sants", "Sant Martí", "Poblenou", "Badalona", "L'Hospitalet"
    };
    private static final String[] NOMBRES_REPARTIDORES = {"Luis Martínez", "Ana Torres", "Jordi Puig", "Marta Vidal"};
    private static final int[] CAPACIDADES = {5, 4, 6, 5};

    private final ZonaLogistica recepcion = new ZonaLogistica("Recepción", 10);
    private final ZonaLogistica almacen = new ZonaLogistica("Almacén", 20);
    private final ZonaLogistica clasificacion = new ZonaLogistica("Clasificación", 10);
    private final ZonaLogistica empaquetado = new ZonaLogistica("Empaquetado", 8);
    private final ZonaLogistica expedicion = new ZonaLogistica("Expedición", 15);
    private final ZonaLogistica reparto = new ZonaLogistica("Reparto", Integer.MAX_VALUE);
    private final ZonaLogistica entregados = new ZonaLogistica("Entregados", Integer.MAX_VALUE);
    private final ZonaLogistica devueltos = new ZonaLogistica("Devueltos", Integer.MAX_VALUE);

    private final ControlSimulacion control = new ControlSimulacion();
    private final Registro registro = new Registro();
    private final Estadisticas estadisticas = new Estadisticas(NOMBRES_REPARTIDORES.length);

    private HiloRecepcion hiloRecepcion;
    private HiloAlmacen hiloAlmacen;
    private Clasificador[] clasificadores;
    private Empaquetador[] empaquetadores;
    private Repartidor[] repartidores;
    private int contadorPaquetes;
    private volatile String estadoSimulacion;

    public CentroLogistico() {
        estadoSimulacion = "LISTO";
        prepararHilos();
    }

    private void prepararHilos() {
        hiloRecepcion = new HiloRecepcion(this);
        hiloAlmacen = new HiloAlmacen(this);
        clasificadores = new Clasificador[3];
        for (int i = 0; i < clasificadores.length; i++) {
            clasificadores[i] = new Clasificador(i + 1, this);
        }
        empaquetadores = new Empaquetador[2];
        for (int i = 0; i < empaquetadores.length; i++) {
            empaquetadores[i] = new Empaquetador(i + 1, this);
        }
        repartidores = new Repartidor[NOMBRES_REPARTIDORES.length];
        for (int i = 0; i < repartidores.length; i++) {
            repartidores[i] = new Repartidor(i + 1, NOMBRES_REPARTIDORES[i], CAPACIDADES[i], i + 1, this);
        }
    }

    private Thread[] todosLosHilos() {
        Thread[] hilos = new Thread[2 + clasificadores.length + empaquetadores.length + repartidores.length];
        int indice = 0;
        hilos[indice++] = hiloRecepcion;
        hilos[indice++] = hiloAlmacen;
        for (Clasificador clasificador : clasificadores) {
            hilos[indice++] = clasificador;
        }
        for (Empaquetador empaquetador : empaquetadores) {
            hilos[indice++] = empaquetador;
        }
        for (Repartidor repartidor : repartidores) {
            hilos[indice++] = repartidor;
        }
        return hilos;
    }

    public void iniciar() {
        control.iniciar();
        for (Thread hilo : todosLosHilos()) {
            hilo.start();
        }
        estadoSimulacion = "EN EJECUCIÓN";
        registro.registrar("Simulación iniciada");
    }

    public void pausar() {
        control.pausar();
        estadoSimulacion = "PAUSADO";
        registro.registrar("Simulación pausada");
    }

    public void reanudar() {
        control.reanudar();
        estadoSimulacion = "EN EJECUCIÓN";
        registro.registrar("Simulación reanudada");
    }

    public void detener() {
        control.detener();
        Thread[] hilos = todosLosHilos();
        for (Thread hilo : hilos) {
            hilo.interrupt();
        }
        for (Thread hilo : hilos) {
            try {
                hilo.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        estadoSimulacion = "DETENIDO";
        registro.registrar("Simulación detenida");
    }

    public void reiniciar() {
        detener();
        recepcion.limpiar();
        almacen.limpiar();
        clasificacion.limpiar();
        empaquetado.limpiar();
        expedicion.limpiar();
        reparto.limpiar();
        entregados.limpiar();
        devueltos.limpiar();
        estadisticas.reiniciar();
        registro.limpiar();
        synchronized (this) {
            contadorPaquetes = 0;
        }
        prepararHilos();
        iniciar();
    }

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

    public int getPendientes() {
        return recepcion.tamanio() + almacen.tamanio();
    }

    public int getEnProceso() {
        int valor = estadisticas.getGenerados() - estadisticas.getEntregados() - estadisticas.getDevueltos() - getPendientes();
        return Math.max(0, valor);
    }

    public String resumenEstadisticas() {
        StringBuilder texto = new StringBuilder();
        texto.append("            ESTADÍSTICAS\n");
        texto.append("------------------------------------\n");
        texto.append(String.format("%-24s%8d%n", "Paquetes generados:", estadisticas.getGenerados()));
        texto.append(String.format("%-24s%8d%n", "Entregados:", estadisticas.getEntregados()));
        texto.append(String.format("%-24s%8d%n", "Devueltos:", estadisticas.getDevueltos()));
        texto.append(String.format("%-24s%8d%n", "En proceso:", getEnProceso()));
        texto.append(String.format("%-24s%8d%n", "Pendientes:", getPendientes()));
        texto.append("\n");
        texto.append(String.format("%-24s%6.1f s%n", "Tiempo promedio:", estadisticas.getTiempoPromedio()));
        texto.append("\n");
        for (int i = 0; i < repartidores.length; i++) {
            texto.append(String.format("%-24s%8d%n", "Repartidor " + (i + 1) + ":", estadisticas.getEntregadosRepartidor(i)));
        }
        ListaEnlazada<Paquete> listaDevueltos = devueltos.copiar();
        if (!listaDevueltos.estaVacia()) {
            texto.append("\nPaquetes devueltos:\n");
            int contador = 0;
            for (Paquete paquete : listaDevueltos) {
                texto.append(paquete.getCodigo()).append("  ");
                contador++;
                if (contador % 4 == 0) {
                    texto.append("\n");
                }
            }
        }
        return texto.toString();
    }

    public ZonaLogistica getRecepcion() {
        return recepcion;
    }

    public ZonaLogistica getAlmacen() {
        return almacen;
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

    public ZonaLogistica getReparto() {
        return reparto;
    }

    public ZonaLogistica getEntregados() {
        return entregados;
    }

    public ZonaLogistica getDevueltos() {
        return devueltos;
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

    public Clasificador[] getClasificadores() {
        return clasificadores;
    }

    public Empaquetador[] getEmpaquetadores() {
        return empaquetadores;
    }

    public Repartidor[] getRepartidores() {
        return repartidores;
    }

    public String getEstadoSimulacion() {
        return estadoSimulacion;
    }
}
