package GUIShapenumber;

/* Reduce.java

 REDUCTOR DE N�MEROS DE FORMA

 2014 05 21 Jes�s Manuel Olivares Ceja

 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

class Reduce extends JFrame {

    JLabel eTitulo = new JLabel("Shape number reduction");
    JButton bCarga = new JButton("Load shape");
    JLabel eNomArch = new JLabel("");
    JLabel eResolucion = new JLabel("Resolution: ");
    JTextField tResolucion = new JTextField("2");
    JButton bAumenta = new JButton("+");
    JButton bReduce = new JButton("-");
    JButton bTransforma = new JButton("Transform");
    String nomArchivo = null;
    JButton bVisualiza = new JButton("Visualization");
    JButton bSalir = new JButton("Exit");
    JTextArea tNumeroForma = new JTextArea("");
    JTextArea tResultado = new JTextArea("");
    Graphics g = null;
    JTextArea texto = new JTextArea("", 5, 100);
// PANELES PARA LA PANTALLA
    JScrollPane panelScroll = new JScrollPane();
    JScrollPane panelSTexto = new JScrollPane(texto);
    Canvas digital = new Canvas();
    Image image = null;
    byte matriz[][];
    int renMax = 0;
    int colMax = 0;
    byte patron[][];
    byte original[][];
    byte referencia[][];
    int resolucion;
    JScrollPane panelLista = new JScrollPane();
    DefaultListModel listaR = new DefaultListModel();
    JList listaReglas = new JList(listaR);
// VARIABLES DE LA TRANSFORMACIÓN DE ESCALA
    ArrayList<Regla> reglas;
    int uReglas;
// POSICION DE LA VENTANA ACTUAL
    int posX = 0;
    int posY = 0;
// ACCIONES
    ActionListener aAumenta = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int valor = Integer.parseInt(tResolucion.getText());

            if (valor < 256) {
                valor <<= 1;
            }
            tResolucion.setText("" + valor);
        }
    };
    ActionListener aReduce = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int valor = Integer.parseInt(tResolucion.getText());

            if (valor > 2) {
                valor >>= 1;
            }
            tResolucion.setText("" + valor);
        }
    };
    ActionListener aTransforma = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // APLICA LAS REGLAS AL N�MERO DE FORMA DE ENTRADA
            tResultado.setText(transforma(tNumeroForma.getText()) );


        }
    };
//------------------------------------------------------------------------------------------
    ActionListener aCarga = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String nomArch = eligeArchivo("Choose the file with CVV code to be transformed");
            eNomArch.setText(nomArch);
            try {
                BufferedReader entrada = new BufferedReader(new FileReader(nomArch));

                String vacio = entrada.readLine();
                vacio = entrada.readLine();
                int res = Integer.parseInt(vacio);
                tResolucion.setText("" + res);
                vacio = entrada.readLine();
                int longitud = Integer.parseInt(vacio);
                String numeroForma = entrada.readLine();
                tNumeroForma.setText(numeroForma);
            } catch (IOException excp) {
            }
        }
    };
    ActionListener aVisualiza = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int ren = 0;
            int col = 0;

            formaPatron();
            //   System.out.println("YA FORME PATRON");

            resolucion = Integer.parseInt(tResolucion.getText());

            Graphics gr = digital.getGraphics();
            int pps = 256 / resolucion;
            //DESPLIEGA EL PATRON EN EL LIENZO (Canvas)
            for (int pr = 0; pr < patron.length; pr++) {
                for (int pc = 0; pc < patron[0].length; pc++) {
                    if (patron[pr][pc] == 0) {
                        gr.setColor(Color.white);
                    } else {
                        gr.setColor(Color.darkGray);
                    }

                    gr.fillRect(pc * pps, pr * pps, pps, pps);
                }
            }
            // DESPLIEGA UNA MALLA PARA EL PATRON DIGITALIZADO
            for (int pr = 0; pr < patron.length; pr++) {
                for (int pc = 0; pc < patron[0].length; pc++) {
                    gr.setColor(Color.lightGray);
                    if (resolucion < 256) {
                        gr.drawRect(pc * pps, pr * pps, pps, pps);
                    }
                }
            }
        }
    };
    ActionListener aSalir = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            dispose();
            //System.exit(0);
        }
    };

// ***********************
// ELIGE IMAGEN A PROCESAR
// ***********************
    String eligeArchivo(String _mensaje) {
        JFileChooser cualArch = new JFileChooser();

        cualArch.setCurrentDirectory(new java.io.File("."));
        cualArch.setDialogTitle(_mensaje);
        cualArch.setFileSelectionMode(JFileChooser.FILES_ONLY);
        cualArch.showOpenDialog(this);
        File nomArch = cualArch.getSelectedFile();
        if (nomArch == null) {
            return null;
        }
        return nomArch.getAbsolutePath();
    }

    void formaPatron() {
        resolucion = Integer.parseInt(tResolucion.getText());

        matriz = new byte[resolucion * 3][resolucion * 3];
        int ir, ic;
        int adelanteX[] = {1, 0, -1, 0};
        int adelanteY[] = {0, 1, 0, -1};
        int arribaAdelanteX[] = {1, 1, -1, -1};
        int arribaAdelanteY[] = {-1, 1, 1, -1};
        int dir = 0;
        int idc = 0, idr = 0; // �ndices de columna y renglon

        int xMin = 0;
        int xMax = 0;
        int yMin = 0;
        int yMax = 0;
//System.out.println("Resolucion "+resolucion);
        // LLENA LA matriz CON CEROS
        for (ir = 0; ir < matriz.length; ir++) {
            for (ic = 0; ic < matriz[0].length; ic++) {
                matriz[ir][ic] = 0;
            }
        }


        String numero = tResultado.getText();
//System.out.println("Este es el n�mero que se transforma: "+numero);

        // SE COLOCA EL PUNTO INICIAL
        idr = resolucion - 1;
        idc = resolucion - 1;
        dir = 0;
        xMin = idc;
        yMin = idr;
        xMax = idc;
        yMax = idr;
//      System.out.println("pongo primer punto");
//      matriz[idr][idc] = 1;
//System.out.println("ya lo puse");

        for (int pos = 1; pos < numero.length(); pos++) {
            char simbolo = numero.charAt(pos);
//System.out.println(""+pos+":  "+simbolo+" "+idr+","+idc);
            // AJUSTA LAS COORDENADAS MINIMAS Y M�XIMAS
            if (idc < xMin) {
                xMin = idc;
            }
            if (idc > xMax) {
                xMax = idc;
            }
            if (idr < yMin) {
                yMin = idr;
            }
            if (idr > yMax) {
                yMax = idr;
            }
//System.out.println(""+xMin+","+yMin+"   "+xMax+","+yMax);
            matriz[idr][idc] = 1;
            switch (simbolo) {
                case '1':
                    dir = (dir + 1) % 4;
                    break;
                case '2':
                    idr += adelanteY[dir];
                    idc += adelanteX[dir];
//              matriz[idr][idc] = 1;
                    break;
                case '3':
                    idr += arribaAdelanteY[dir];
                    idc += arribaAdelanteX[dir];
                    //            matriz[idr][idc] = 1;
                    dir = (dir + 3) % 4;
                    break;
            }
        }
//System.out.println();


        // COPIA EL AREA EN EL PATRON
        patron = new byte[resolucion][resolucion];
        int ipx = 0, ipy = 0;
        for (idr = yMin; idr <= yMax; idr++) {
            for (idc = xMin; idc <= xMax; idc++) {
                patron[ipy][ipx] = matriz[idr][idc];
                ipx++;
            }
            ipy++;
            ipx = 0;
        }
    }

    String transforma(String cadenaEntrada) {
        int nodo = 1;
        String cadenaSalida = new String();

        System.out.println("Se pueden aplicar " + uReglas + " reglas");

        Cola cola = new Cola();
        Estado inicio = new Estado();

        inicio.nodo = nodo;
        inicio.padre = 0;

        inicio.antes = new String("INICIO");
        inicio.regla = 0;
        inicio.posicion = 0;
        //inicio.despues = new String("p1111121212122222212");
        inicio.despues = cadenaEntrada;
        //new String("p112123321213211223311332311213131213213123112323");

        cola.inserta(inicio);
        while (cola.vacia() == false) {
            // TOMA UN NODO DE LA COLA
            Estado actual = (Estado) cola.retira();

            // SE APLICAN LAS REGLAS
            for (int iRegla = 0; iRegla < uReglas; iRegla++) {
                
                StringBuffer nueva = new StringBuffer();
                //              System.out.println("#### iRegla="+iRegla);
                Estado nuevo = new Estado();

                // PREPARA LOS DATOS DEL ESTADO A LOS QUE SE APLICARA LA REGLA
                nuevo.antes = actual.despues;
                nuevo.regla = iRegla;
                nuevo.posicion = actual.posicion;
                StringBuffer cadena = new StringBuffer(actual.despues);

                // TOMA UNA REGLA
                String antes = reglas.get(iRegla).antes;
                String despues = reglas.get(iRegla).despues;
                int avance = reglas.get(iRegla).avance;

//                System.out.println("ANTES: " + cadena);
                if (cadena.toString().substring(nuevo.posicion).startsWith(antes)) {
                    System.out.println("\nAplicando la regla: " + iRegla);
                    nuevo.nodo = ++nodo;
                    nuevo.padre = actual.nodo;
                    System.out.println("nodo: " + nuevo.nodo + "   padre: " + nuevo.padre);
                    if (nuevo.posicion > 0) {
                        nueva = new StringBuffer(cadena.toString().substring(0, nuevo.posicion)
                                + despues + cadena.substring(nuevo.posicion + antes.length()));
                    } else {
                        nueva = new StringBuffer(despues + cadena.substring(antes.length()));
                    }
                    nuevo.posicion += avance;
                    System.out.println("ANTES: " + cadena);
                    System.out.println("DESPUÉS: " + nueva);
                    cadena = new StringBuffer(nueva);
                    nuevo.despues = new String(cadena);
                    
                    cadenaSalida = nuevo.despues;

                    cola.inserta(nuevo);
                }
            }
        }
        return cadenaSalida;
    }

//----------------------------------------------------------------------------------------------------------
    public Reduce() {
        Container cont = getContentPane();
        Font fTitulo = new Font("Arial", Font.BOLD, 18);
        Font fNormal = new Font("Arial", Font.BOLD, 12);

        Dimension tamano = Toolkit.getDefaultToolkit().getScreenSize();
        int XPmax = 800; //(int)(tamano.width * 0.9);
        int YPmax = 600; //(int)(tamano.height * 0.9);
        int centrar = 0;
        int longitud = 0;
//System.out.println("ALTO "+YPmax+" ANCHO "+XPmax);

        cont.setLayout(null); // DISTRIBUCI�N DEL CONTENEDOR LIBRE
        cont.setBackground(Color.white); // FONDO BLANCO

        eTitulo.setFont(fTitulo);
        cont.add(eTitulo);
        FontMetrics tamTexto = eTitulo.getFontMetrics(eTitulo.getFont());
        longitud = tamTexto.stringWidth(eTitulo.getText());
        centrar = (XPmax - longitud) / 2;
        eTitulo.setBounds(centrar, 10, longitud, 40);

        bCarga.setFont(fNormal);
        cont.add(bCarga);
        bCarga.setBounds(20, 50, 100, 30);

        eNomArch.setFont(fNormal);
        cont.add(eNomArch);
        eNomArch.setBounds(130, 50, 550, 30);

        eResolucion.setFont(fNormal);
        cont.add(eResolucion);
        eResolucion.setBounds(20, 95, 150, 30);

        tResolucion.setFont(fNormal);
        cont.add(tResolucion);
        tResolucion.setEditable(false);
        tResolucion.setBounds(90, 95, 30, 30);

        bAumenta.setFont(fNormal);
        cont.add(bAumenta);
        bAumenta.setBounds(125, 95, 41, 13);
        bAumenta.addActionListener(aAumenta);

        bReduce.setFont(fNormal);
        cont.add(bReduce);
        bReduce.setBounds(125, 112, 41, 13);
        bReduce.addActionListener(aReduce);

        bVisualiza.setFont(fNormal);
        cont.add(bVisualiza);
        bVisualiza.setBounds(500, 95, 150, 30);

        cont.add(digital);
        digital.setBounds(430, 140, 260, 260);

        tNumeroForma.setFont(fNormal);
        cont.add(tNumeroForma);
        tNumeroForma.setLineWrap(true);
        tNumeroForma.setBounds(20, 150, 350, 60);


        bTransforma.setFont(fNormal);
        cont.add(bTransforma);
        bTransforma.setBounds(145, 370, 100, 30);

        tResultado.setFont(fNormal);
        cont.add(tResultado);
        tResultado.setLineWrap(true);
        tResultado.setBounds(20, 420, 350, 60);

        bSalir.setFont(fNormal);
        cont.add(bSalir);
        bSalir.setBounds(610, 450, 100, 30);

        // ASIGNA LAS ACCIONES
        bCarga.addActionListener(aCarga);
        bVisualiza.addActionListener(aVisualiza);
        bTransforma.addActionListener(aTransforma);
        bSalir.addActionListener(aSalir);


        // CARGA DE REGLAS
        reglas = new ArrayList<Regla>();
        uReglas = 0;

        cont.add(panelLista);
        panelLista.setViewportView(listaReglas);
        panelLista.setBounds(20, 230, 350, 120);

        try {
            BufferedReader lexico = new BufferedReader(new FileReader("..\\datos\\reglas.txt"));
            int numeroRegla = 0;

            while (lexico.ready()) {
                String lineaLexico = lexico.readLine();

                if (lineaLexico.length() < 1) {
                    continue;
                }
                Regla regla = new Regla();
                StringBuffer unaRegla = new StringBuffer();

                regla.antes = new String(lineaLexico);

                lineaLexico = lexico.readLine();
                regla.despues = new String(lineaLexico);
                lineaLexico = lexico.readLine();
                regla.avance = Integer.parseInt(lineaLexico);

                unaRegla.append("" + numeroRegla + "  (" + regla.avance + ") ");
                unaRegla.append(regla.antes + " --> ");
                unaRegla.append(regla.despues);
                numeroRegla++;

                listaR.addElement(unaRegla.toString());

                reglas.add(regla);
                uReglas++;
            }
        } catch (IOException e) {
        }
        listaR.addElement("    Loaded " + uReglas + " rules");

        // ACCION PARA CUANDO SE PIDE EL CIERRE DE LA VENTANA
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });


    }

    // **********
    // tomaPatron
    // **********
    void tomaPatron(byte[][] _matriz, int _ren, int _col) {
        Graphics g2 = image.getGraphics();

        Cola cola = new Cola();
        int renVecino[] = {-1, -1, -1, 0, 0, 1, 1, 1};
        int colVecino[] = {-1, 0, 1, -1, 1, -1, 0, 1};
        int colMin = _col;
        int renMin = _ren;
        int colMax = _col;
        int renMax = _ren;

        // MARCA COMO PROCESADO EL PIXEL
        matriz[_ren][_col] = 2;
        cola.inserta(new Par(_ren, _col));
        while (cola.vacia() == false) {
            Par par = (Par) cola.retira();

            // APLICA LA VECINDAD PARA ENCONTRAR EL PATRON
            for (int v = 0; v < renVecino.length; v++) {
                if (par.ren + renVecino[v] < 0 || par.ren + renVecino[v] >= matriz.length) {
                    continue;
                }
                if (par.col + colVecino[v] < 0 || par.col + colVecino[v] >= matriz[0].length) {
                    continue;
                }

                // SI EXISTE UN VECINO == 1 LO ENCOLA
                if (matriz[par.ren + renVecino[v]][par.col + colVecino[v]] == 1) {
                    cola.inserta(new Par(par.ren + renVecino[v], par.col + colVecino[v]));
                    matriz[par.ren + renVecino[v]][par.col + colVecino[v]] = 2;
                    // RECALCULA LOS LIMITES DEL PATRON
                    if ((par.ren + renVecino[v]) < renMin) {
                        renMin = par.ren + renVecino[v];
                    }
                    if ((par.ren + renVecino[v]) > renMax) {
                        renMax = par.ren + renVecino[v];
                    }
                    if ((par.col + colVecino[v]) < colMin) {
                        colMin = par.col + colVecino[v];
                    }
                    if ((par.col + colVecino[v]) > colMax) {
                        colMax = par.col + colVecino[v];
                    }
                }
            }
        }

        // DETERMINA EL TAMA�O DE LA MATRIZ DEL PATRON OBTENIDO
        original = new byte[renMax - renMin + 1][colMax - colMin + 1];

        if ((renMax - renMin + 1) > 1 && (colMax - colMin + 1) > 1) {
            for (int iR = 0; iR < (renMax - renMin + 1); iR++) {
                for (int iC = 0; iC < (colMax - colMin + 1); iC++) {
                    original[iR][iC] = matriz[iR + renMin][iC + colMin];
                }
            }
        } else {
            // SI NO SE ENCONTR� UN PATR�N USA UN PIXEL
            original = new byte[1][1];
        }

        // COLOCA UN RECT�NGULO ROJO SOBRE LA IMAGEN DETECTADA
        g2.setColor(Color.red);
        g2.drawRect(colMin, renMin, colMax - colMin, renMax - renMin);
        Icon imageIcono = new ImageIcon(image);
        JLabel label = new JLabel(imageIcono);
        panelScroll.getViewport().add(label);

        // SE CALCULA EL �REA DE RECONOCIMIENTO CREANDO OTRA MATRIZ
        int factorC = colMax - colMin + 1;
        int factorR = renMax - renMin + 1;

        // DETERMINA EL FACTOR MAYOR
        int factor = factorC;
        if (factorR > factorC) {
            factor = factorR;
        }

        // GENERA UNA MATRIZ CUADRADA
        if (factor > 1) {
            referencia = new byte[factor][factor];
        } else {
            referencia = new byte[1][1];
        }

        // PASA EL PATRON ORIGINAL A LA MATRIZ CUADRADA
        for (int iR = 0; iR < factorR; iR++) {
            for (int iC = 0; iC < factorC; iC++) {
                referencia[iR][iC] = (byte) (matriz[renMin + iR][colMin + iC] == 2 ? 1 : 0);
            }
        }
    }

    /*
     public static void main(String args[])
     {
     Dimension tamano = Toolkit.getDefaultToolkit().getScreenSize();

     Reduce ventanaPrincipal = new Reduce();

     //sETexto.setSize((int)(tamano.width * 0.9), (int)(tamano.height * 0.9));
     ventanaPrincipal.setSize(800,600);
     ventanaPrincipal.setLocationRelativeTo(null);
     ventanaPrincipal.setVisible(true);
     }
     */
}