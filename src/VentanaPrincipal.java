import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class VentanaPrincipal extends JFrame {

    private static final Color AZUL = new Color(45, 75, 125);
    private static final Color VERDE = new Color(60, 170, 90);
    private static final Color NARANJA = new Color(240, 150, 30);
    private static final Color ROJO = new Color(210, 60, 50);

    private final CentroLogistico centro;

    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnReanudar;
    private JButton btnDetener;
    private JButton btnReiniciar;
    private JButton btnEstadisticas;
    private JLabel lblResumen;

    private JProgressBar barRecepcion;
    private JProgressBar barAlmacen;
    private JProgressBar barClasificacion;
    private JProgressBar barEmpaquetado;
    private JProgressBar barExpedicion;
    private PanelPaquetes panelRecepcion;
    private PanelPaquetes panelAlmacen;
    private PanelPaquetes panelClasificacion;
    private PanelPaquetes panelEmpaquetado;
    private final PanelPaquetes[] panelesRutas = new PanelPaquetes[4];
    private final JLabel[] lblClasificadores = new JLabel[3];
    private final JLabel[] lblEmpaquetadores = new JLabel[2];

    private final JLabel[] lblRepNombre = new JLabel[4];
    private final JLabel[] lblRepEstado = new JLabel[4];
    private final JLabel[] lblRepCarga = new JLabel[4];
    private final JLabel[] lblRepEntregados = new JLabel[4];
    private final JLabel[] lblRepLleno = new JLabel[4];
    private final JProgressBar[] barRepartidores = new JProgressBar[4];
    private final PanelPaquetes[] panelesCarga = new PanelPaquetes[4];

    private JTextArea areaRegistro;

    public VentanaPrincipal() {
        super("Sistema de Paquetería - Centro Logístico");
        centro = new CentroLogistico();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearRegistro(), BorderLayout.SOUTH);
        configurarBotones(true, false, false, false, false);
        Timer temporizador = new Timer(250, e -> actualizar());
        temporizador.start();
        setSize(1350, 980);
        setLocationRelativeTo(null);
        actualizar();
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(AZUL);
        cabecera.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel titulo = new JLabel("CENTRO LOGÍSTICO - SISTEMA DE PAQUETERÍA", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        botones.setOpaque(false);
        btnIniciar = crearBoton("INICIAR", VERDE);
        btnPausar = crearBoton("PAUSAR", NARANJA);
        btnReanudar = crearBoton("REANUDAR", new Color(50, 130, 200));
        btnDetener = crearBoton("DETENER", ROJO);
        btnReiniciar = crearBoton("REINICIAR", new Color(120, 90, 170));
        btnEstadisticas = crearBoton("ESTADÍSTICAS", new Color(70, 70, 70));
        botones.add(btnIniciar);
        botones.add(btnPausar);
        botones.add(btnReanudar);
        botones.add(btnDetener);
        botones.add(btnReiniciar);
        botones.add(btnEstadisticas);

        btnIniciar.addActionListener(e -> {
            centro.iniciar();
            configurarBotones(false, true, false, true, true);
        });
        btnPausar.addActionListener(e -> {
            centro.pausar();
            configurarBotones(false, false, true, true, true);
        });
        btnReanudar.addActionListener(e -> {
            centro.reanudar();
            configurarBotones(false, true, false, true, true);
        });
        btnDetener.addActionListener(e -> {
            centro.detener();
            configurarBotones(false, false, false, false, true);
        });
        btnReiniciar.addActionListener(e -> {
            areaRegistro.setText("");
            centro.reiniciar();
            configurarBotones(false, true, false, true, true);
        });
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setOpaque(false);
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leyenda.setOpaque(false);
        JLabel textoLeyenda = new JLabel("Prioridad:");
        textoLeyenda.setForeground(Color.WHITE);
        leyenda.add(textoLeyenda);
        for (Prioridad prioridad : Prioridad.values()) {
            JLabel etiqueta = new JLabel(" " + prioridad.name() + " ");
            etiqueta.setOpaque(true);
            etiqueta.setBackground(prioridad.getColor());
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11));
            leyenda.add(etiqueta);
        }
        lblResumen = new JLabel();
        lblResumen.setForeground(Color.WHITE);
        lblResumen.setFont(new Font("SansSerif", Font.BOLD, 13));
        inferior.add(leyenda, BorderLayout.WEST);
        inferior.add(lblResumen, BorderLayout.EAST);

        cabecera.add(titulo);
        cabecera.add(botones);
        cabecera.add(inferior);
        return cabecera;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(140, 34));
        return boton;
    }

    private void configurarBotones(boolean iniciar, boolean pausar, boolean reanudar, boolean detener, boolean reiniciar) {
        btnIniciar.setEnabled(iniciar);
        btnPausar.setEnabled(pausar);
        btnReanudar.setEnabled(reanudar);
        btnDetener.setEnabled(detener);
        btnReiniciar.setEnabled(reiniciar);
    }

    private JPanel crearSeccion(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AZUL, 2), titulo);
        borde.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        borde.setTitleColor(AZUL);
        panel.setBorder(borde);
        return panel;
    }

    private JProgressBar crearBarra() {
        JProgressBar barra = new JProgressBar();
        barra.setStringPainted(true);
        barra.setFont(new Font("SansSerif", Font.BOLD, 11));
        return barra;
    }

    private JLabel crearEtiquetaTrabajador() {
        JLabel etiqueta = new JLabel();
        etiqueta.setFont(new Font("Monospaced", Font.BOLD, 12));
        return etiqueta;
    }

    private JPanel crearZona(String titulo, JProgressBar barra, PanelPaquetes paquetes, JPanel extra) {
        JPanel seccion = crearSeccion(titulo);
        seccion.add(barra, BorderLayout.NORTH);
        seccion.add(paquetes, BorderLayout.CENTER);
        if (extra != null) {
            seccion.add(extra, BorderLayout.SOUTH);
        }
        return seccion;
    }

    private JPanel crearCentro() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        JPanel fila = new JPanel(new GridLayout(1, 3, 8, 8));
        barRecepcion = crearBarra();
        panelRecepcion = new PanelPaquetes(115);
        fila.add(crearZona("RECEPCIÓN", barRecepcion, panelRecepcion, null));
        barAlmacen = crearBarra();
        panelAlmacen = new PanelPaquetes(115);
        fila.add(crearZona("ALMACÉN", barAlmacen, panelAlmacen, null));
        barClasificacion = crearBarra();
        panelClasificacion = new PanelPaquetes(60);
        JPanel clasificadores = new JPanel(new GridLayout(3, 1));
        for (int i = 0; i < lblClasificadores.length; i++) {
            lblClasificadores[i] = crearEtiquetaTrabajador();
            clasificadores.add(lblClasificadores[i]);
        }
        fila.add(crearZona("CLASIFICACIÓN", barClasificacion, panelClasificacion, clasificadores));
        contenedor.add(fila);

        barEmpaquetado = crearBarra();
        panelEmpaquetado = new PanelPaquetes(35);
        JPanel empaquetadores = new JPanel(new GridLayout(1, 2));
        for (int i = 0; i < lblEmpaquetadores.length; i++) {
            lblEmpaquetadores[i] = crearEtiquetaTrabajador();
            empaquetadores.add(lblEmpaquetadores[i]);
        }
        contenedor.add(crearZona("EMPAQUETADO", barEmpaquetado, panelEmpaquetado, empaquetadores));

        JPanel expedicion = crearSeccion("EXPEDICIÓN");
        barExpedicion = crearBarra();
        expedicion.add(barExpedicion, BorderLayout.NORTH);
        JPanel rutas = new JPanel(new GridLayout(1, panelesRutas.length, 8, 8));
        for (int i = 0; i < panelesRutas.length; i++) {
            JPanel ruta = new JPanel(new BorderLayout());
            ruta.setBorder(BorderFactory.createTitledBorder("Ruta " + (i + 1)));
            panelesRutas[i] = new PanelPaquetes(62);
            ruta.add(panelesRutas[i], BorderLayout.CENTER);
            rutas.add(ruta);
        }
        expedicion.add(rutas, BorderLayout.CENTER);
        contenedor.add(expedicion);

        JPanel reparto = crearSeccion("REPARTO");
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 8, 8));
        for (int i = 0; i < 4; i++) {
            tarjetas.add(crearTarjetaRepartidor(i));
        }
        reparto.add(tarjetas, BorderLayout.CENTER);
        contenedor.add(reparto);
        return contenedor;
    }

    private JPanel crearTarjetaRepartidor(int i) {
        JPanel tarjeta = new JPanel(new BorderLayout(3, 3));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        JPanel datos = new JPanel(new GridLayout(6, 1));
        lblRepNombre[i] = new JLabel();
        lblRepNombre[i].setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRepEstado[i] = new JLabel();
        lblRepEstado[i].setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRepCarga[i] = new JLabel();
        barRepartidores[i] = crearBarra();
        lblRepLleno[i] = new JLabel();
        lblRepLleno[i].setForeground(ROJO);
        lblRepLleno[i].setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRepEntregados[i] = new JLabel();
        datos.add(lblRepNombre[i]);
        datos.add(lblRepEstado[i]);
        datos.add(lblRepCarga[i]);
        datos.add(barRepartidores[i]);
        datos.add(lblRepLleno[i]);
        datos.add(lblRepEntregados[i]);
        panelesCarga[i] = new PanelPaquetes(62);
        tarjeta.add(datos, BorderLayout.NORTH);
        tarjeta.add(panelesCarga[i], BorderLayout.CENTER);
        return tarjeta;
    }

    private JScrollPane crearRegistro() {
        areaRegistro = new JTextArea();
        areaRegistro.setEditable(false);
        areaRegistro.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaRegistro.setBackground(new Color(25, 30, 40));
        areaRegistro.setForeground(new Color(160, 230, 160));
        JScrollPane scroll = new JScrollPane(areaRegistro);
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AZUL, 2), "REGISTRO DEL SISTEMA");
        borde.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        borde.setTitleColor(AZUL);
        scroll.setBorder(borde);
        scroll.setPreferredSize(new Dimension(100, 170));
        return scroll;
    }

    private void actualizarBarra(JProgressBar barra, ZonaLogistica zona, int ocupados) {
        int capacidad = zona.getCapacidad();
        barra.setMaximum(capacidad);
        barra.setValue(Math.min(ocupados, capacidad));
        barra.setString(ocupados + " / " + capacidad + " paquetes");
        double porcentaje = (double) ocupados / capacidad;
        if (porcentaje < 0.6) {
            barra.setForeground(VERDE);
        } else if (porcentaje < 0.9) {
            barra.setForeground(NARANJA);
        } else {
            barra.setForeground(ROJO);
        }
    }

    private void actualizarZona(ZonaLogistica zona, JProgressBar barra, PanelPaquetes panel) {
        ListaEnlazada<Paquete> copia = zona.copiar();
        actualizarBarra(barra, zona, copia.tamanio());
        panel.setPaquetes(copia);
    }

    private String textoTrabajador(String nombre, Paquete paquete) {
        if (paquete == null) {
            return " " + nombre + " → libre";
        }
        return " " + nombre + " → " + paquete.getCodigo() + " [" + paquete.getPrioridad() + "]";
    }

    private void actualizar() {
        actualizarZona(centro.getRecepcion(), barRecepcion, panelRecepcion);
        actualizarZona(centro.getAlmacen(), barAlmacen, panelAlmacen);
        actualizarZona(centro.getClasificacion(), barClasificacion, panelClasificacion);
        actualizarZona(centro.getEmpaquetado(), barEmpaquetado, panelEmpaquetado);

        ListaEnlazada<Paquete> expedicion = centro.getExpedicion().copiar();
        actualizarBarra(barExpedicion, centro.getExpedicion(), expedicion.tamanio());
        for (int i = 0; i < panelesRutas.length; i++) {
            int ruta = i + 1;
            panelesRutas[i].setPaquetes(expedicion.filtrar(p -> p.getRuta() == ruta));
        }

        Clasificador[] clasificadores = centro.getClasificadores();
        for (int i = 0; i < lblClasificadores.length; i++) {
            lblClasificadores[i].setText(textoTrabajador(clasificadores[i].getName(), clasificadores[i].getActual()));
        }
        Empaquetador[] empaquetadores = centro.getEmpaquetadores();
        for (int i = 0; i < lblEmpaquetadores.length; i++) {
            lblEmpaquetadores[i].setText(textoTrabajador(empaquetadores[i].getName(), empaquetadores[i].getActual()));
        }

        Repartidor[] repartidores = centro.getRepartidores();
        for (int i = 0; i < repartidores.length; i++) {
            Repartidor repartidor = repartidores[i];
            ListaEnlazada<Paquete> carga = repartidor.getCarga().copiar();
            int capacidad = repartidor.getCapacidad();
            lblRepNombre[i].setText("R" + repartidor.getIdentificador() + " - " + repartidor.getNombre() + " (Ruta " + repartidor.getRuta() + ")");
            lblRepEstado[i].setText("Estado: " + repartidor.getEstado().getTexto());
            lblRepEstado[i].setForeground(repartidor.getEstado().getColor());
            lblRepCarga[i].setText("Capacidad: " + carga.tamanio() + "/" + capacidad);
            barRepartidores[i].setMaximum(capacidad);
            barRepartidores[i].setValue(carga.tamanio());
            barRepartidores[i].setString(carga.tamanio() + "/" + capacidad);
            barRepartidores[i].setForeground(carga.tamanio() >= capacidad ? ROJO : VERDE);
            lblRepLleno[i].setText(carga.tamanio() >= capacidad ? "[ LLENO ]" : " ");
            lblRepEntregados[i].setText("Entregados: " + repartidor.getEntregados());
            panelesCarga[i].setPaquetes(carga);
        }

        Estadisticas estadisticas = centro.getEstadisticas();
        lblResumen.setText("Estado: " + centro.getEstadoSimulacion()
                + "   |   Generados: " + estadisticas.getGenerados()
                + "   Entregados: " + estadisticas.getEntregados()
                + "   Devueltos: " + estadisticas.getDevueltos()
                + "   En proceso: " + centro.getEnProceso() + "  ");

        ListaEnlazada<String> nuevas = centro.getRegistro().vaciar();
        if (!nuevas.estaVacia()) {
            for (String linea : nuevas) {
                areaRegistro.append(linea + "\n");
            }
            int sobrantes = areaRegistro.getLineCount() - 400;
            if (sobrantes > 0) {
                try {
                    areaRegistro.replaceRange("", 0, areaRegistro.getLineEndOffset(sobrantes - 1));
                } catch (BadLocationException e) {
                    areaRegistro.setText("");
                }
            }
            areaRegistro.setCaretPosition(areaRegistro.getDocument().getLength());
        }
    }

    private void mostrarEstadisticas() {
        JTextArea texto = new JTextArea(centro.resumenEstadisticas());
        texto.setEditable(false);
        texto.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(texto);
        scroll.setPreferredSize(new Dimension(380, 380));
        JOptionPane.showMessageDialog(this, scroll, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }
}