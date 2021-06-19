package ResuelveLaberinto;

/* ResuelveLaberinto.java

 RESUELVE LABERINTOS

 2014 05 21 Jes�s Manuel Olivares Ceja

 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ResuelveLaberinto extends JFrame {

    JLabel eTitulo = new JLabel("Solucionador de laberintos");

    JButton bCarga = new JButton("Carga datos");
    JLabel eNomArch = new JLabel("");

    JButton bBuscaPlan = new JButton("Busca solución");
    JButton bSalir = new JButton("Salir");

    String nomArchivo = null;

    JTextArea taMensaje = new JTextArea("");
    JScrollPane sMensaje = null; // new JScrollPane(taMensaje);

// PANELES PARA LA PANTALLA
    JScrollPane panelScroll = new JScrollPane();

    Canvas digital = new Canvas();
    Graphics g = null;

    Image image = null;

    byte matriz[][];
    int renMax = 0;
    int colMax = 0;
    int resolucion;

    int nivelMaximo = 0;

// ACCIONES
    ActionListener aCarga = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String nomArch = eligeArchivo("Seleccione el laberinto a utilizar");
            // SE COLOCA EN EN LA ETIQUETA eNomArch EL ARCHIVO CON SU 
            eNomArch.setText(nomArch);

            try {
                // ABRE EL ARCHIVO DE ENTRADA
                BufferedReader entrada = new BufferedReader(new FileReader(nomArch));

                // TOMA LA RESOLUCIÓN DEL ARCHIVO
                String linea = entrada.readLine();
                // si linea es numerico SINO marca error
                resolucion = Integer.parseInt(linea);

                // CARGA LOS DATOS EN LA matriz
                int ren = 0, col = 0;
                renMax = resolucion;
                colMax = resolucion;
                // SE LE ASIGNA MEMORIA A LA matriz
                matriz = new byte[resolucion][resolucion];

                // CARGA DATOS
                while (entrada.ready()) {
                    // LEE UN REGISTRO    
                    linea = entrada.readLine();

                    // SEPARA LA ENTRADA USANDO EL ESPACIO COMO SEPARADOR
                    StringTokenizer punto = new StringTokenizer(linea, " ");
                    // POR CADA SIMBOLO LEIDO (token) AVANZA UNA COLUMNA
                    col = 0;
                    while (punto.hasMoreTokens()) {
                        String unPunto = punto.nextToken();

                        if (col == resolucion) {
                            continue;
                        }
                        matriz[ren][col] = (byte) Integer.parseInt(unPunto);
                        col++;
                    }
                    // AVANZA UN RENGLON POR EL REGISTRO LEIDO
                    ren++;
                }

                // DESPLIEGA EL TABLERO
                // pps CONTIENE CUÁNTOS PIXELES POR CADA CUADRITO (resolucion=16)
                //int pps = 256 / resolucion;
                int pps = (digital.getWidth() - 2) / resolucion;
                Graphics gr = digital.getGraphics();

                gr.setColor(Color.blue);
                gr.drawRect(0, 0, resolucion * pps + 1, resolucion * pps + 1);

                for (ren = 0; ren < renMax; ren++) {
                    for (col = 0; col < colMax; col++) {
                        switch (matriz[ren][col]) {
                            case 0:
                                gr.setColor(Color.white);
                                break;
                            case 1:
                                gr.setColor(Color.darkGray);
                                break;
                            case 2: // ESTADO INICIAL
                                gr.setColor(Color.red);
                                break;
                            case 3: // ESTACIÓN DESTINO
                                gr.setColor(Color.green);
                                break;
                            case 4: //estaciones de la línea X
                                gr.setColor(Color.yellow);
                                break;
                            case 5: //estaciones de la línea Y
                                gr.setColor(Color.pink);
                                break;
                        }
                        gr.fillRect(col * pps + 1, ren * pps + 1, pps, pps);
                    }
                }
                // CIERRA EL ARCHIVO
                entrada.close();
                bBuscaPlan.setEnabled(true);
            } catch (IOException ex) {
                taMensaje.setText("ERROR al leer el archivo de entrada");
            }
        }
    };

    ActionListener aBuscaPlan = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // LIMPIA LOS MENSAJES 
            taMensaje.setText("");

            // INVOCA A planea PARA ENCONTRAR UN PLAN
            planea();
        }
    };

    ActionListener aSalir = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            //dispose();
            System.exit(0);
        }
    };

// ***********************
// ELIGE LABERINTO A RESOLVER 
// ***********************
    String eligeArchivo(String mensaje) {
        // JFileChooser ES UNA VENTANA DE DIALOGO PARA ELEGIR UN ARCHIVO
        JFileChooser cualArch = new JFileChooser();

        cualArch.setCurrentDirectory(new java.io.File("."));
        cualArch.setDialogTitle(mensaje);
        // LE DECIMOS QUE ELIJA SOLAMENTE ARCHIVOS
        cualArch.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // MUESTRA LA VENTANA DE DIALOGO EN this VENTANA
        cualArch.showOpenDialog(this);
        // TOMA EL NOMBRE Y RUTA (PATH) DEL ARCHIVO ELEGIDO
        File nomArch = cualArch.getSelectedFile();
        if (nomArch == null) {
            return null;
        }
        return nomArch.getAbsolutePath();
    }

    public ResuelveLaberinto() {
        // cont APUNTA A LA INTERFAZ GRÁFICA DE LA VENTANA DEL JFrame 
        Container cont = getContentPane();
        cont.setLayout(null); // DISTRIBUCIÓN DEL CONTENEDOR LIBRE
        cont.setBackground(Color.white); // FONDO BLANCO

        // SE DECLARAN DOS TIPOS DE LETRA 
        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fNormal = new Font("Arial", Font.BOLD, 12);

        // VUELVE A TOMAR EL TAMAÑO DE LA PANTALLA
        Dimension tamano = Toolkit.getDefaultToolkit().getScreenSize();
        int XPmax = 800; //(int)(tamano.width * 0.9);
        int YPmax = 600; //(int)(tamano.height * 0.9);
        int centrar = 0;
        int longitud = 0;

        eTitulo.setFont(fTitulo);
        cont.add(eTitulo);
        // TOMA LA MEDIDA DEL TIPO DE LETRA
        FontMetrics tamTexto = eTitulo.getFontMetrics(eTitulo.getFont());
        // TOMA EL TAMAÑO QUE OCUPA EL TITULO EN LA VENTANA
        longitud = tamTexto.stringWidth(eTitulo.getText());
        // CENTRA EL TITULO EN LA VENTANA
        centrar = (XPmax - longitud) / 2;
        // COLOCA EL TÍTULO CENTRADO EN LA VENTANA EN EL RENGLÓN 10
        eTitulo.setBounds(centrar, 10, longitud, 40);

        // COLOCA EL BOTÓN Carga datos
        bCarga.setFont(fNormal);
        cont.add(bCarga);
        bCarga.setBounds(20, 50, 120, 30);

        // COLOCA LA ETIQUETA DEL NOMBRE DEL ARCHIVO
        eNomArch.setFont(fNormal);
        cont.add(eNomArch);
        eNomArch.setBounds(150, 50, 550, 30);

        // COLOCA EL BOTÓN Busca plan
        bBuscaPlan.setFont(fNormal);
        cont.add(bBuscaPlan);
        bBuscaPlan.setBounds(500, 95, 150, 30);
        // EL BOTON Busca plan INICIA DESACTIVADO
        bBuscaPlan.setEnabled(false);

        // COLOCA EL LIENZO (canvas) DONDE SE PINTA EL LABERINTO 
        cont.add(digital);
        digital.setBounds(40, 140, 280, 280);

        // COLOCA EL ÁREA DE MENSAJES DE TEXTO EN LA VENTANA CON Scroll
        sMensaje = new JScrollPane(taMensaje);
        cont.add(sMensaje);
        sMensaje.setBounds(370, 140, 400, 280); //360, 250);

        // COLOCA EL BOTÓN Salir
        bSalir.setFont(fNormal);
        cont.add(bSalir);
        bSalir.setBounds(610, 500, 140, 30);

        // ACCION PARA CUANDO SE PIDE EL CIERRE DE LA VENTANA
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0); // dispose();
            }
        });
        // he colocado los componentes gráficos en la ventana

        // ASIGNA LAS ACCIONES A CADA BOTÓN
        bCarga.addActionListener(aCarga);
        bBuscaPlan.addActionListener(aBuscaPlan);
        bSalir.addActionListener(aSalir);
    }

    boolean planea() {
        // ESTE ALGORITMO DEBE REALIZAR LA BÚSQUEDA EN AMPLITUD
        int inicioRen = -1, inicioCol = -1; // ESTADO INICIAL
        int metaRen = -1, metaCol = -1; // ESTADO FINAL 

        int ren, col;
        Cola cola = new Cola();
        int accion;
        ArrayList visitados = new ArrayList<Estado>();

        // UBICA EN DONDE SE ENCUENTRA EL ESTADO INICIAL DEL MÓVIL
        for (ren = 0; ren < renMax; ren++) {
            for (col = 0; col < colMax; col++) {
                if (matriz[ren][col] == 2) {
                    inicioRen = ren;
                    inicioCol = col;
                    break;
                }
            }
        }
        taMensaje.append("(" + inicioRen + ", " + inicioCol + ") Posición inicial\n\n");

        // UBICA EN DONDE SE ENCUENTRA la Estación destino
        for (ren = 0; ren < renMax; ren++) {
            for (col = 0; col < colMax; col++) {
                if (matriz[ren][col] == 3) {
                    metaRen = ren;
                    metaCol = col;
                    break;
                }
            }
        }
        taMensaje.append("(" + metaRen + ", " + metaCol + ") Posición meta \n\n");

        // VALIDACIÓN PARA VERIFICAR QUE HAY ESTADO INICIAL Y ESTADO FINAL 
        if (inicioRen == -1 || inicioCol == -1) {
            taMensaje.append("Posición inicial o meta con error\n");
            bBuscaPlan.setEnabled(false);
            return false;
        }

        // INICIA LA BÚSQUEDA EN AMPLITUD
        Estado inicial = new Estado(++Estado.contadorId, 0, inicioRen, inicioCol, 0);
        boolean continua = true;

        // INICIA LA BÚSQUEDA EN AMPLITUD
        cola.inserta(inicial);
        while (continua && cola.vacia() == false) {
            Estado actual = (Estado) cola.retira();
            taMensaje.append("(" + actual.ren + ", " + actual.col + ") Revisando\n");

            // COMPARA EL ESTADO actual CON EL ESTADO FINAL, SI SON IGUALES TERMINÓ
            if (actual.ren == metaRen && actual.col == metaCol) {
                taMensaje.append("        AÑADO A VISITADOS: (" + actual.ren + "," + actual.col + ")\n");
                visitados.add(actual);  // GUARDA EL ESTADO META EN visitados
                taMensaje.append("" + actual.id + "(" + actual.ren + "," + actual.col + ") META  Padre: " + actual.padre + " Nivel: " + actual.nivel + "\n\n"); //*
                guardaPlan(actual.id, visitados, metaRen, metaCol); // DESPLIEGA EL PLAN ENCONTRADO
                return false;
            }

            // REVISA LAS ACCIONES (o reglas) QUE SE PUEDAN APLICAR AL ESTADO actual
            for (accion = 0; accion < 4; accion++) {
                if (aplicaAccion(accion, actual) == true) {
                    Estado nuevo = generaEstado(accion, actual, visitados);
                    if (nuevo != null) {
                        if (estaEnVistados(nuevo, visitados) == false) {
                            cola.inserta(nuevo);
                            taMensaje.append("usar\n");
                        } else {
                            taMensaje.append("NO usar\n");
                        }
                    }
                }
            }

            // GUARDA EL ESTADO actual EN LA LISTA DE NODOS visitados
            taMensaje.append("        AÑADO A VISITADOS: (" + actual.ren + "," + actual.col + ")\n");
            visitados.add(actual);
        }
        return false;
    }

    int posicionEstado(int id, ArrayList visitados) {
        int n = visitados.size();
        for (int i = 0; i < n; i++) {
            Estado estado = (Estado) visitados.get(i);
            if (estado.id == id) {
                return i;
            }
        }
        return -1;
    }

    void guardaPlan(int meta, ArrayList visitados, int ADYRen, int ADYCol) {
        ArrayList elPlan = new ArrayList<Estado>();

        // DESPLIEGA EL TABLERO
        //int pps = 256 / resolucion;
        int pps = (digital.getWidth() - 2) / resolucion;
        Graphics gr = digital.getGraphics();

        taMensaje.append("ENCONTRE LA META\n");

        int cual = meta;
        boolean continua = true;
        while (continua) {
            int p = posicionEstado(cual, visitados);
            if (p == -1) {
                break;
            }
            Estado plan = (Estado) visitados.get(p);
            taMensaje.append("#### id=" + plan.id + " (" + plan.ren + ", " + plan.col + ")  acción=" + plan.accion + "  padre=" + plan.padre + "\n");
            elPlan.add(plan);

            if (plan.padre == 0) {
                continua = false;
                taMensaje.append("Encontré el primer nodo\n");
            } else {
                cual = plan.padre;
            }
        }

        taMensaje.append("\n\nEL PLAN ES:\n");

        int longitudPlan = elPlan.size();
        Estado plan = (Estado) elPlan.get(longitudPlan - 1);
        taMensaje.append("\tInicio en (" + plan.ren + "," + plan.col + ") " + plan.nivel + "\n");

        for (int j = longitudPlan - 2; j > -1; j--) {
            plan = (Estado) elPlan.get(j);
            String nombreAccion = null;

            switch (plan.accion) {
                case 0:
                    nombreAccion = "<-izquierda ";
                    break;
                case 1:
                    nombreAccion = "^ arriba    ";
                    break;
                case 2:
                    nombreAccion = "-> derecha  ";
                    break;
                case 3:
                    nombreAccion = "v abajo     ";
                    break;
                default:
                    nombreAccion = "#### acción erronea";
                    break;

            }
            taMensaje.append("\t" + nombreAccion + " ==> (" + plan.ren + "," + plan.col + ") " + plan.nivel + "\n");

            if (plan.ren == ADYRen && plan.col == ADYCol) {
                continue;
            }
            // PINTA EL RESULTADO DEL PLAN
            gr.setColor(Color.cyan);
            gr.fillRect(plan.col * pps + 1, plan.ren * pps + 1, pps, pps);
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                //Thread.currentThread().interrupt();
            }
        }
        taMensaje.append("\tFin del plan\n");
        nivelMaximo = 0;
        int n = visitados.size();
        for (int i = 0; i < n; i++) {
            Estado estado = (Estado) visitados.get(i);
            if (estado.nivel > nivelMaximo) {
                nivelMaximo = estado.nivel;
            }
        }
        nivelMaximo++;
        taMensaje.append("Nivel máximo: " + nivelMaximo);

        // CONTAMOS EL NÚMERO DE NODOS EN CADA NIVEL
        taMensaje.append("\nNODOS POR NIVEL DEL ÁRBOL:\n");
        int nodosVisitados = visitados.size();
        for (int iNivel = 0; iNivel < nivelMaximo; iNivel++) {
            int cuenta = 0;
            for (int i = 0; i < nodosVisitados; i++) {
                Estado estado = (Estado) visitados.get(i);
                if (estado.nivel == iNivel) {
                    cuenta++;
                }
            }
            taMensaje.append("Nivel: " + iNivel + " Número de nodos:" + cuenta + "\n");
        }

        // CREAMOS UN OBJETO PARA VISUALIZAR EL ARBOL
        ArbolGrafico arbol = new ArbolGrafico(visitados, nivelMaximo, elPlan);
    }

    boolean estaEnVistados(Estado actual, ArrayList<Estado> visitados) {
        boolean esta = false;
        taMensaje.append("BUSCANDO (" + actual.ren + "," + actual.col + ") ");
        int n = visitados.size();
        taMensaje.append("Hay " + n + " nodos ");
        for (int i = 0; (i < n) && (esta == false); i++) {
            Estado visitado = (Estado) visitados.get(i);
            taMensaje.append("(" + visitado.ren + "," + visitado.col + ") ");
            if (visitado.ren == actual.ren && visitado.col == actual.col) {
                taMensaje.append("Repetido");
                esta = true;
            }
        }
        taMensaje.append("\n");
        return esta;
    }

    Estado generaEstado(int accion, Estado actual, ArrayList visitados) {
        int ren = actual.ren, col = actual.col;
        switch (accion) {
            case 0:  // IZQ
                col--;
                taMensaje.append("IZQ ");
                break;
            case 1:  // ARRIBA
                ren--;
                taMensaje.append("ARRIBA ");
                break;
            case 2:  // DER
                col++;
                taMensaje.append("DER ");
                break;
            case 3:  // ABAJO
                ren++;
                taMensaje.append("ABAJO ");
                break;
        }
        taMensaje.append(" (" + ren + "," + col + ") ");
        Estado nuevo = new Estado(++Estado.contadorId, actual.id, ren, col, actual.nivel + 1);
        nuevo.accion = accion; // ME GENERÉ CON ESTA ACCIÓN, SOY EL ESTADO nuevo
        return nuevo;
    }

    boolean aplicaAccion(int accion, Estado estado) {
        switch (accion) {
            case 0: // IZQUIERDA
                if (estado.col > 0) {
                    if (matriz[estado.ren][estado.col - 1] == 0
                            || matriz[estado.ren][estado.col - 1] == 3) {
                        matriz[estado.ren][estado.col] = 0;
                        matriz[estado.ren][estado.col - 1] = 2;
                        return true;
                    }
                }
                break;
            case 1: // ARRIBA
                if (estado.ren > 0) {
                    if (matriz[estado.ren - 1][estado.col] == 0
                            || matriz[estado.ren - 1][estado.col] == 3) {
                        matriz[estado.ren][estado.col] = 0;
                        matriz[estado.ren - 1][estado.col] = 2;
                        return true;
                    }
                }
                break;
            case 2: // DERECHA
                if (estado.col < (colMax - 1)) {
                    if (matriz[estado.ren][estado.col + 1] == 0
                            || matriz[estado.ren][estado.col + 1] == 3) {
                        matriz[estado.ren][estado.col] = 0;
                        matriz[estado.ren][estado.col + 1] = 2;
                        return true;
                    }
                }
                break;
            case 3: // ABAJO
                if (estado.ren < (renMax - 1)) {
                    if (matriz[estado.ren + 1][estado.col] == 0
                            || matriz[estado.ren + 1][estado.col] == 3) {
                        matriz[estado.ren][estado.col] = 0;
                        matriz[estado.ren + 1][estado.col] = 2;
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public static void main(String args[]) {
        // OBTIENE EL TAMAÑO DE LA PANTALLA
        Dimension tamano = Toolkit.getDefaultToolkit().getScreenSize();

        // SE INVOCA AL CONSTRUCTOR DEL OBJETO ventanaPrincipal 
        ResuelveLaberinto ventanaPrincipal = new ResuelveLaberinto();

// ASIGNA EL TAMAÑO DE LA VENTANA DEL PROGRAMA AL 70% = 0.7 DE LA PANTALLA   
// ventanaPrincipal.setSize((int) (tamano.width * 0.7), (int) (tamano.height * 0.7));
        // ASIGNA EL TAMAÑO DE LA VENTANA A 800x600 PIXELES
        ventanaPrincipal.setSize(800, 600);

        // CENTRA LA VENTANA EN LA PANTALLA
        ventanaPrincipal.setLocationRelativeTo(null);

        // SE HACE VISIBLE LA VENTANA EN LA PANTALLA
        ventanaPrincipal.setVisible(true);

    }

}
