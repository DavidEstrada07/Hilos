import java.util.Random;

public class Repartidor extends Thread {

    private final int identificador;
    private final String nombre;
    private final int capacidad;
    private final int ruta;
    private final CentroLogistico centro;
    private final ZonaLogistica carga;
    private final Random random = new Random();
    private volatile EstadoRepartidor estado;
    private volatile int entregados;

    public Repartidor(int identificador, String nombre, int capacidad, int ruta, CentroLogistico centro) {
        super("Repartidor-" + identificador);
        this.identificador = identificador;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ruta = ruta;
        this.centro = centro;
        this.carga = new ZonaLogistica("Vehículo " + identificador, capacidad);
        this.estado = EstadoRepartidor.DISPONIBLE;
        this.entregados = 0;
    }

    @Override
    public void run() {
        ControlSimulacion control = centro.getControl();
        try {
            while (control.isActivo()) {
                control.esperarSiPausado();
                if (carga.estaVacia()) {
                    estado = EstadoRepartidor.DISPONIBLE;
                    cargar(centro.getExpedicion().extraer(p -> p.getRuta() == ruta));
                }
                estado = EstadoRepartidor.CARGANDO;
                while (carga.tamanio() < capacidad) {
                    Paquete paquete = centro.getExpedicion().extraer(p -> p.getRuta() == ruta, 5000);
                    if (paquete == null) {
                        break;
                    }
                    cargar(paquete);
                }
                if (carga.tamanio() >= capacidad) {
                    centro.getRegistro().registrar(getName() + " LLENO (" + carga.tamanio() + "/" + capacidad + ")");
                }
                salirARuta();
                estado = EstadoRepartidor.REGRESANDO;
                centro.getRegistro().registrar(getName() + " regresando al centro");
                control.dormir(1500);
            }
        } catch (InterruptedException e) {
        }
        estado = EstadoRepartidor.FUERA_DE_SERVICIO;
    }

    private void cargar(Paquete paquete) throws InterruptedException {
        paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
        carga.agregar(paquete);
        centro.getReparto().agregar(paquete);
        centro.getRegistro().registrar(paquete.getCodigo() + " asignado a " + getName());
        centro.getControl().dormir(400);
    }

    private void salirARuta() throws InterruptedException {
        ControlSimulacion control = centro.getControl();
        ListaEnlazada<Paquete> paquetes = carga.copiar();
        for (Paquete paquete : paquetes) {
            if (paquete.getEstado() == EstadoPaquete.NUEVO_INTENTO) {
                paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
            }
        }
        estado = EstadoRepartidor.EN_RUTA;
        centro.getRegistro().registrar(getName() + " inicia Ruta " + ruta + " con " + paquetes.tamanio() + " paquetes");
        control.dormir(2000);
        for (Paquete paquete : paquetes) {
            estado = EstadoRepartidor.ENTREGANDO;
            control.dormir(1200);
            if (random.nextDouble() < 0.2) {
                int intentos = paquete.registrarIntentoFallido();
                centro.getRegistro().registrar(paquete.getCodigo() + " intento " + intentos + " → Cliente ausente");
                if (intentos >= 3) {
                    paquete.cambiarEstado(EstadoPaquete.DEVUELTO);
                    carga.quitar(paquete);
                    centro.getReparto().quitar(paquete);
                    centro.getDevueltos().agregar(paquete);
                    centro.getEstadisticas().sumarDevuelto();
                    centro.getRegistro().registrar(paquete.getCodigo() + " DEVUELTO tras 3 intentos fallidos");
                } else {
                    paquete.cambiarEstado(EstadoPaquete.NUEVO_INTENTO);
                }
            } else {
                paquete.cambiarEstado(EstadoPaquete.ENTREGADO);
                carga.quitar(paquete);
                centro.getReparto().quitar(paquete);
                centro.getEntregados().agregar(paquete);
                entregados++;
                centro.getEstadisticas().sumarEntregado(identificador - 1, System.currentTimeMillis() - paquete.getTiempoCreacion());
                centro.getRegistro().registrar(paquete.getCodigo() + " entregado por " + getName());
            }
            estado = EstadoRepartidor.EN_RUTA;
        }
    }

    public int getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getRuta() {
        return ruta;
    }

    public EstadoRepartidor getEstado() {
        return estado;
    }

    public int getEntregados() {
        return entregados;
    }

    public ZonaLogistica getCarga() {
        return carga;
    }
}
