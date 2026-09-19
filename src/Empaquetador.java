public class Empaquetador extends Thread {

    private final CentroLogistico centro;
    private volatile Paquete actual;

    public Empaquetador(int numero, CentroLogistico centro) {
        super("Empaquetador-" + numero);
        this.centro = centro;
    }

    @Override
    public void run() {
        ControlSimulacion control = centro.getControl();
        try {
            while (control.isActivo()) {
                control.esperarSiPausado();
                Paquete paquete = centro.getEmpaquetado().reservar(p -> p.getEstado() == EstadoPaquete.CLASIFICADO, EstadoPaquete.EMPAQUETANDO);
                actual = paquete;
                centro.getRegistro().registrar(paquete.getCodigo() + " empaquetando en " + getName() + " (" + paquete.getPeso() + " kg)");
                control.dormir(tiempoEmpaquetado(paquete.getPeso()));
                paquete.cambiarEstado(EstadoPaquete.EMPAQUETADO);
                centro.getRegistro().registrar(paquete.getCodigo() + " empaquetado");
                centro.getExpedicion().esperarEspacio();
                paquete.cambiarEstado(EstadoPaquete.EN_EXPEDICION);
                centro.getExpedicion().agregar(paquete);
                centro.getEmpaquetado().quitar(paquete);
                actual = null;
                centro.getRegistro().registrar(paquete.getCodigo() + " en expedición → " + paquete.getRutaTexto());
            }
        } catch (InterruptedException e) {
        }
        actual = null;
    }

    private long tiempoEmpaquetado(double peso) {
        if (peso <= 2) {
            return 1000;
        }
        if (peso <= 5) {
            return 2000;
        }
        return 3000;
    }

    public Paquete getActual() {
        return actual;
    }
}
