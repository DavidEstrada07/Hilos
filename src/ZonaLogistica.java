import java.util.Comparator;
import java.util.function.Predicate;

public class ZonaLogistica {

    private static final Comparator<Paquete> POR_PRIORIDAD = Comparator.comparingInt(p -> p.getPrioridad().ordinal());

    private final String nombre;
    private final int capacidad;
    private final ListaEnlazada<Paquete> paquetes;

    public ZonaLogistica(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.paquetes = new ListaEnlazada<>();
    }

    public synchronized void agregar(Paquete paquete) throws InterruptedException {
        while (paquetes.tamanio() >= capacidad) {
            wait();
        }
        paquetes.agregar(paquete);
        notifyAll();
    }

    public synchronized void esperarEspacio() throws InterruptedException {
        while (paquetes.tamanio() >= capacidad) {
            wait();
        }
    }

    public synchronized Paquete extraer(Predicate<Paquete> condicion) throws InterruptedException {
        Paquete paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        while (paquete == null) {
            wait();
            paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        }
        paquetes.eliminar(paquete);
        notifyAll();
        return paquete;
    }

    public synchronized Paquete extraer(Predicate<Paquete> condicion, long espera) throws InterruptedException {
        long limite = System.currentTimeMillis() + espera;
        Paquete paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        while (paquete == null) {
            long restante = limite - System.currentTimeMillis();
            if (restante <= 0) {
                return null;
            }
            wait(restante);
            paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        }
        paquetes.eliminar(paquete);
        notifyAll();
        return paquete;
    }

    public synchronized Paquete reservar(Predicate<Paquete> condicion, EstadoPaquete nuevoEstado) throws InterruptedException {
        Paquete paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        while (paquete == null) {
            wait();
            paquete = paquetes.buscarMejor(condicion, POR_PRIORIDAD);
        }
        paquete.cambiarEstado(nuevoEstado);
        notifyAll();
        return paquete;
    }

    public synchronized void quitar(Paquete paquete) {
        if (paquetes.eliminar(paquete)) {
            notifyAll();
        }
    }

    public synchronized ListaEnlazada<Paquete> copiar() {
        return paquetes.copiar();
    }

    public synchronized int tamanio() {
        return paquetes.tamanio();
    }

    public synchronized boolean estaVacia() {
        return paquetes.estaVacia();
    }

    public synchronized void limpiar() {
        paquetes.limpiar();
        notifyAll();
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getNombre() {
        return nombre;
    }
}
