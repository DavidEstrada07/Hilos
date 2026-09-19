public class HiloAlmacen extends Thread {

    private final CentroLogistico centro;

    public HiloAlmacen(CentroLogistico centro) {
        super("Almacen");
        this.centro = centro;
    }

    @Override
    public void run() {
        ControlSimulacion control = centro.getControl();
        try {
            while (control.isActivo()) {
                control.dormir(500);
                centro.getAlmacen().esperarEspacio();
                Paquete paquete = centro.getRecepcion().extraer(p -> true);
                control.esperarSiPausado();
                paquete.cambiarEstado(EstadoPaquete.ALMACENADO);
                centro.getAlmacen().agregar(paquete);
                centro.getRegistro().registrar(paquete.getCodigo() + " almacenado");
            }
        } catch (InterruptedException e) {
        }
    }
}
