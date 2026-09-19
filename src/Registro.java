import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Registro {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ListaEnlazada<String> pendientes = new ListaEnlazada<>();

    public synchronized void registrar(String mensaje) {
        pendientes.agregar(LocalTime.now().format(FORMATO) + " | " + mensaje);
    }

    public synchronized ListaEnlazada<String> vaciar() {
        ListaEnlazada<String> copia = pendientes.copiar();
        pendientes.limpiar();
        return copia;
    }

    public synchronized void limpiar() {
        pendientes.limpiar();
    }
}
