import java.util.Random;

public class HiloRecepcion extends Thread {

    private static final String[] CLIENTES = {
            "Carlos López", "María García", "Jordi Puig", "Laura Martínez", "Pau Serra", "Ana Fernández",
            "David Romero", "Núria Vidal", "Javier Ruiz", "Elena Soler", "Marc Ferrer", "Lucía Moreno"
    };
    private static final String[] CALLES = {
            "Carrer de Mallorca", "Avinguda Diagonal", "Carrer de Balmes", "Gran Via", "Carrer d'Aragó",
            "Passeig de Gràcia", "Carrer de Sants", "Rambla del Poblenou"
    };

    private final CentroLogistico centro;
    private final Random random = new Random();

    public HiloRecepcion(CentroLogistico centro) {
        super("Recepcion");
        this.centro = centro;
    }

    @Override
    public void run() {
        ControlSimulacion control = centro.getControl();
        try {
            while (control.isActivo()) {
                control.esperarSiPausado();
                Paquete paquete = generarPaquete();
                if (centro.getRecepcion().tamanio() >= centro.getRecepcion().getCapacidad()) {
                    centro.getRegistro().registrar("Recepción llena, esperando espacio");
                }
                centro.getRecepcion().agregar(paquete);
                centro.getEstadisticas().sumarGenerado();
                centro.getRegistro().registrar(paquete.getCodigo() + " recibido [" + paquete.getPrioridad() + "] → " + paquete.getCiudad());
                control.dormir(700 + random.nextInt(900));
            }
        } catch (InterruptedException e) {
        }
    }

    private Paquete generarPaquete() {
        String cliente = CLIENTES[random.nextInt(CLIENTES.length)];
        String direccion = CALLES[random.nextInt(CALLES.length)] + ", " + (1 + random.nextInt(250));
        String ciudad = CentroLogistico.ZONAS[random.nextInt(CentroLogistico.ZONAS.length)];
        double peso = Math.round((0.2 + random.nextDouble() * 7.8) * 10) / 10.0;
        return new Paquete(centro.siguienteCodigo(), cliente, direccion, ciudad, peso, generarPrioridad());
    }

    private Prioridad generarPrioridad() {
        int valor = random.nextInt(100);
        if (valor < 10) {
            return Prioridad.URGENTE;
        }
        if (valor < 30) {
            return Prioridad.ALTA;
        }
        if (valor < 75) {
            return Prioridad.NORMAL;
        }
        return Prioridad.BAJA;
    }
}
