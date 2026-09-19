
public class Paquete {

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final String ciudad;
    private final double peso;
    private final Prioridad prioridad;
    private final long tiempoCreacion;
    private EstadoPaquete estado;
    private int ruta;
    private int intentos;

    public Paquete(String codigo, String cliente, String direccion, String ciudad, double peso, Prioridad prioridad) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado = EstadoPaquete.RECIBIDO;
        this.ruta = 0;
        this.intentos = 0;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    public synchronized void cambiarEstado(EstadoPaquete nuevo) {
        if (!estado.puedeCambiarA(nuevo)) {
            throw new IllegalStateException("Transición inválida en " + codigo + ": " + estado + " → " + nuevo);
        }
        estado = nuevo;
    }

    public synchronized int registrarIntentoFallido() {
        intentos++;
        return intentos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public double getPeso() {
        return peso;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public long getTiempoCreacion() {
        return tiempoCreacion;
    }

    public synchronized EstadoPaquete getEstado() {
        return estado;
    }

    public synchronized int getRuta() {
        return ruta;
    }

    public synchronized void setRuta(int ruta) {
        this.ruta = ruta;
    }

    public synchronized int getIntentos() {
        return intentos;
    }

    public String getRutaTexto() {
        int valor = getRuta();
        return valor == 0 ? "Sin asignar" : String.format("R%02d", valor);
    }

    public String detalleHtml() {
        return "<html><b>Código:</b> " + codigo
                + "<br><b>Cliente:</b> " + cliente
                + "<br><b>Dirección:</b> " + direccion
                + "<br><b>Ciudad:</b> " + ciudad
                + "<br><b>Peso:</b> " + peso + " kg"
                + "<br><b>Prioridad:</b> " + prioridad
                + "<br><b>Estado:</b> " + getEstado()
                + "<br><b>Ruta:</b> " + getRutaTexto()
                + "<br><b>Intentos:</b> " + getIntentos() + "</html>";
    }

    @Override
    public String toString() {
        return codigo;
    }
}
