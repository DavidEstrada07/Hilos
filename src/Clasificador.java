import java.util.Random;

public class Clasificador extends Thread {

    private final CentroLogistico centro;
    private final Random random = new Random();
    private volatile Paquete actual;

    public Clasificador(int numero, CentroLogistico centro) {
        super("Clasificador-" + numero);
        this.centro = centro;
    }

    @Override
    public void run() {
        ControlSimulacion control = centro.getControl();
        try {
            while (control.isActivo()) {
                control.esperarSiPausado();
                Paquete paquete = centro.getAlmacen().extraer(p -> true);
                paquete.cambiarEstado(EstadoPaquete.CLASIFICANDO);
                centro.getClasificacion().agregar(paquete);
                actual = paquete;
                centro.getRegistro().registrar(paquete.getCodigo() + " tomado por " + getName());
                control.dormir(1200 + random.nextInt(1200));
                int ruta = CentroLogistico.rutaPara(paquete.getCiudad());
                paquete.setRuta(ruta);
                paquete.cambiarEstado(EstadoPaquete.CLASIFICADO);
                centro.getRegistro().registrar(paquete.getCodigo() + " clasificado (" + paquete.getCiudad() + ") → Ruta " + ruta);
                centro.getEmpaquetado().agregar(paquete);
                centro.getClasificacion().quitar(paquete);
                actual = null;
            }
        } catch (InterruptedException e) {
        }
        actual = null;
    }

    public Paquete getActual() {
        return actual;
    }
}
