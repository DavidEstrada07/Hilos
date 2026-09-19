import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

public class PanelPaquetes extends JPanel {

    private static final int ANCHO = 80;
    private static final int ALTO = 22;
    private static final int MARGEN = 5;

    private ListaEnlazada<Paquete> paquetes = new ListaEnlazada<>();

    public PanelPaquetes(int altoPreferido) {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(150, altoPreferido));
        setToolTipText("");
    }

    public void setPaquetes(ListaEnlazada<Paquete> paquetes) {
        this.paquetes = paquetes;
        repaint();
    }

    private int columnas() {
        return Math.max(1, (getWidth() - MARGEN) / (ANCHO + MARGEN));
    }

    private int filas() {
        return Math.max(1, (getHeight() - MARGEN) / (ALTO + MARGEN));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics metricas = g2.getFontMetrics();
        int columnas = columnas();
        int maximo = columnas * filas();
        int indice = 0;
        for (Paquete paquete : paquetes) {
            int x = MARGEN + (indice % columnas) * (ANCHO + MARGEN);
            int y = MARGEN + (indice / columnas) * (ALTO + MARGEN);
            if (indice == maximo - 1 && paquetes.tamanio() > maximo) {
                String resto = "+" + (paquetes.tamanio() - indice) + " más";
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(resto, x + (ANCHO - metricas.stringWidth(resto)) / 2, y + (ALTO + metricas.getAscent()) / 2 - 2);
                break;
            }
            g2.setColor(paquete.getPrioridad().getColor());
            g2.fillRoundRect(x, y, ANCHO, ALTO, 10, 10);
            EstadoPaquete estado = paquete.getEstado();
            if (estado == EstadoPaquete.CLASIFICANDO || estado == EstadoPaquete.EMPAQUETANDO || estado == EstadoPaquete.NUEVO_INTENTO) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(x, y, ANCHO, ALTO, 10, 10);
                g2.setStroke(new BasicStroke(1f));
            }
            g2.setColor(Color.BLACK);
            String texto = paquete.getCodigo();
            g2.drawString(texto, x + (ANCHO - metricas.stringWidth(texto)) / 2, y + (ALTO + metricas.getAscent()) / 2 - 2);
            indice++;
        }
    }

    @Override
    public String getToolTipText(MouseEvent evento) {
        if (evento.getX() < MARGEN || evento.getY() < MARGEN) {
            return null;
        }
        int columna = (evento.getX() - MARGEN) / (ANCHO + MARGEN);
        int fila = (evento.getY() - MARGEN) / (ALTO + MARGEN);
        if (columna >= columnas()) {
            return null;
        }
        int indice = fila * columnas() + columna;
        if (indice >= paquetes.tamanio()) {
            return null;
        }
        return paquetes.obtener(indice).detalleHtml();
    }
}
