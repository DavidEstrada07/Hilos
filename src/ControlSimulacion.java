public class ControlSimulacion {

    private volatile boolean activo;
    private boolean pausado;

    public synchronized void iniciar() {
        activo = true;
        pausado = false;
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void reanudar() {
        pausado = false;
        notifyAll();
    }

    public synchronized void detener() {
        activo = false;
        pausado = false;
        notifyAll();
    }

    public boolean isActivo() {
        return activo;
    }

    public synchronized boolean isPausado() {
        return pausado;
    }

    public synchronized void esperarSiPausado() throws InterruptedException {
        while (pausado && activo) {
            wait();
        }
    }

    public void dormir(long milisegundos) throws InterruptedException {
        long restante = milisegundos;
        while (restante > 0) {
            esperarSiPausado();
            long paso = Math.min(100, restante);
            Thread.sleep(paso);
            restante -= paso;
        }
    }
}
