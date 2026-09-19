public class Estadisticas {

    private int generados;
    private int entregados;
    private int devueltos;
    private long tiempoTotal;
    private final int[] porRepartidor;

    public Estadisticas(int repartidores) {
        porRepartidor = new int[repartidores];
    }

    public synchronized void sumarGenerado() {
        generados++;
    }

    public synchronized void sumarEntregado(int repartidor, long tiempo) {
        entregados++;
        tiempoTotal += tiempo;
        porRepartidor[repartidor]++;
    }

    public synchronized void sumarDevuelto() {
        devueltos++;
    }

    public synchronized int getGenerados() {
        return generados;
    }

    public synchronized int getEntregados() {
        return entregados;
    }

    public synchronized int getDevueltos() {
        return devueltos;
    }

    public synchronized int getEntregadosRepartidor(int repartidor) {
        return porRepartidor[repartidor];
    }

    public synchronized double getTiempoPromedio() {
        return entregados == 0 ? 0 : tiempoTotal / 1000.0 / entregados;
    }

    public synchronized void reiniciar() {
        generados = 0;
        entregados = 0;
        devueltos = 0;
        tiempoTotal = 0;
        for (int i = 0; i < porRepartidor.length; i++) {
            porRepartidor[i] = 0;
        }
    }
}
