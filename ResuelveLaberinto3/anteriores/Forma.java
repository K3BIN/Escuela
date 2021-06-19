package GUIShapenumber;

/* Forma.java

   GENERADOR DE N�MEROS DE FORMA

2014 05 21 Jes�s Manuel Olivares Ceja

*/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

class Forma extends JFrame
{


JLabel eTitulo = new JLabel("Shape number generation");
JButton bCarga = new JButton("Load image");
JLabel eNomArch = new JLabel("");

JLabel eResolucion = new JLabel("Resolution: ");
JTextField tResolucion = new JTextField("2");

JButton bAumenta = new JButton("+");
JButton bReduce = new JButton("-");

String nomArchivo = null;
JButton bReconoce = new JButton("Digitalization");
JButton bForma = new JButton("Shape Number");
JButton bSalir = new JButton("Exit");
JButton bGuarda = new JButton("Save");

JTextArea tNumeroForma = new JTextArea("");

JTextField tLongitud = new JTextField("");

Graphics g = null;

JTextArea texto = new JTextArea("",5,100);

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

int resolucion ;

int raizX = 0 ,raizY = 0;

// POSICION DE LA VENTANA ACTUAL
int posX = 0;
int posY = 0;

String numeroForma = null;

// ACCIONES
ActionListener aAumenta = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
    int valor = Integer.parseInt(tResolucion.getText());

        if( valor < 256 )
            valor <<= 1;
        tResolucion.setText(""+valor);
    }
};

ActionListener aReduce = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
    int valor = Integer.parseInt(tResolucion.getText());

        if( valor > 2 )
            valor >>= 1;
        tResolucion.setText(""+valor);
    }
};

 MouseListener eventoRaton = new MouseListener()
  {
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mousePressed(MouseEvent e)
    {
int x = e.getX();
int y = e.getY();
int pps = 256 / resolucion;

int renglon = y / pps;
int columna = x / pps;

     //   System.out.println(""+y+","+x +"    "+renglon+","+columna);
        patron[renglon][columna] = (byte)(patron[renglon][columna] == 1 ? 0 : 1);
        Graphics gr = digital.getGraphics();
        if( patron[renglon][columna] == 0 )
                                gr.setColor(Color.white);
                            else
                                gr.setColor(Color.darkGray);

                            gr.fillRect(columna*pps,renglon*pps,pps,pps);
                             gr.setColor(Color.lightGray);
                           if(resolucion < 256 )
                                 gr.drawRect(columna*pps,renglon*pps,pps,pps);



    }
  };

ActionListener aCarga = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
    String nomArch = eligeArchivo("Seleccione la imagen a reconocer");
    int i = 0;
        for( i = nomArch.length() - 1; i > 0; i-- )
            if( nomArch.charAt(i) == '\\' )
                break;
        nomArchivo = nomArch;
    String nombreArch = nomArch.substring(i);
        eNomArch.setText(nombreArch);
//JOptionPane.showMessageDialog(null, "Voy a cargar la imagen");
// REFERENCIA: http://chuwiki.chuidiang.org/index.php?title=JOptionPane_y_di%C3%A1logos_modales
    //Image image = null;
    BufferedImage imagenTrabajo = null;
        try {
            // Read from a file
        File file1 = new File(nomArchivo);
             image = ImageIO.read(file1);
            imagenTrabajo = ImageIO.read(file1);
       // image = Toolkit.getDefaultToolkit().createImage(imagenTrabajo.getSource());
// AQUI TOME LA IMAGEN VOLVIENDO A LEER PERO SE PUEDE TOMAR LA DE TOOLKIT
        }
        catch (IOException ex2)
        {
            JOptionPane.showMessageDialog(null, "Error en la carga de la imagen");
            //System.out.println("ERROR EN LA CARGA DE LA IMAGEN");
        }

        // DESPLIEGA LA IMAGEN A PROCESAR EN EL PANEL CON SCROLLBAR
        Icon imageIcono = new ImageIcon( image );
        JLabel label = new JLabel( imageIcono );
        panelScroll.getViewport().add( label );
        // REFERENCIA: http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node63.html

//System.out.println("VA BIEN");

        // CONVIERTE LA IMAGEN LEIDA EN UNA MATRIZ DE UNOS Y CEROS
int w = imagenTrabajo.getWidth();
int h = imagenTrabajo.getHeight();
renMax = h;
colMax = w;
     matriz = new byte [h][w];
double promedio = 0;
int n =0;
   for(int col = 0; col < w; col++)
    {
    for(int ren = 0; ren < h; ren++)
    {
        int gris = (int) (
           (double)(
             ( (imagenTrabajo.getRGB(col,ren) & 0xff0000) >> 16 )
           + ( (imagenTrabajo.getRGB(col,ren) & 0x00ff00) >> 8 )
           + ( imagenTrabajo.getRGB(col,ren) & 0x0000ff )
           ) / 3.0);
        promedio += gris;
        n++;
    }
    }
    promedio = promedio / (double)n;

    promedio = promedio * 0.8;

    for(int col = 0; col < w; col++)
    {
    for(int ren = 0; ren < h; ren++)
    {
        int gris = (int) (
           (double)(
             ( (imagenTrabajo.getRGB(col,ren) & 0xff0000) >> 16 )
           + ( (imagenTrabajo.getRGB(col,ren) & 0x00ff00) >> 8 )
           + ( imagenTrabajo.getRGB(col,ren) & 0x0000ff )
           ) / 3.0);
        if( gris > promedio )
            matriz[ren][col] = 0; // BLANCO
        else
            matriz[ren][col] = 1;
    }
}
    }
};

// GENERA EL N�MERO DE FORMA -------------------------------------------------------------------------
ActionListener aForma = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
       numeroForma = calculaNF(patron);

        tLongitud.setText(""+numeroForma.length() );

    }
};

// GUARDA EN UN ARCHIVO EL N�MERO DE FORMA -------------------------------------------------------------------------
ActionListener aGuarda = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
        try
        {
    BufferedWriter salida = new BufferedWriter(new FileWriter("..\\datos\\shape.txt",false));

        salida.write("Numero de forma\n");
        salida.write(""+resolucion+"\n");
        salida.write(""+numeroForma.length()+"\n");
        salida.write(""+numeroForma.toString()+"\n" );
        salida.close();
        }
        catch(IOException excepti)
        {
        }
    }
};


// GENERA LA IMAGEN DISCRETIZADA DE ACUERDO CON LA RESOLUCI�N
ActionListener aReconoce = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
    int ren = 0;
    int col = 0;

        resolucion = Integer.parseInt(tResolucion.getText());
        // RECORRE LA IMAGEN TOMANDO UN PATR�N EN CADA PIXEL
    int hayImagen = 1;

        for( ren = 0; ren < renMax && hayImagen == 1; ren++)
        {
            for( col = 0; col < colMax && hayImagen == 1; col++)
            {
                if( matriz[ren][col] == 1 )
                {
                    tomaPatron(matriz ,ren ,col);

                    // SE TIENEN DOS MATRICES original Y referencia = CUADRADA
                Escala escala = new Escala(referencia);
                patron = escala.escalaBinario(resolucion, resolucion);
        hayImagen = 0;

                    Graphics gr = digital.getGraphics();
                    int pps = 256 / resolucion;



                //DESPLIEGA EL PATRON EN EL LIENZO (Canvas)

                    for(int pr=0; pr < patron.length; pr++)
                        for(int pc=0; pc < patron[0].length; pc++)
                        {
                            if( patron[pr][pc] == 0 )
                                gr.setColor(Color.white);
                            else
                                gr.setColor(Color.darkGray);

                            gr.fillRect(pc*pps,pr*pps,pps,pps);
                        }
               // DESPLIEGA UNA MALLA PARA EL PATRON DIGITALIZADO
                    for(int pr=0; pr < patron.length; pr++)
                        for(int pc=0; pc < patron[0].length; pc++)
                        {
                            gr.setColor(Color.lightGray);
                           if(resolucion < 256 )
                                 gr.drawRect(pc*pps,pr*pps,pps,pps);
                        }



                }
            }
        }

    }
};





ActionListener aSalir = new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
        dispose();
        //System.exit(0);
    }
};

// ***********************
// ELIGE IMAGEN A PROCESAR
// ***********************
String eligeArchivo(String _mensaje)
{
JFileChooser cualArch = new JFileChooser();

    cualArch.setCurrentDirectory(new java.io.File("..\\."));
    cualArch.setDialogTitle(_mensaje);
    cualArch.setFileSelectionMode(JFileChooser.FILES_ONLY);
    cualArch.showOpenDialog(this);
 File nomArch = cualArch.getSelectedFile();
    if( nomArch == null )
        return null;
    return nomArch.getAbsolutePath();
 }


String calculaNF(byte [][]patron)
{
byte matriz[][] = new byte[patron.length + 2][patron.length + 2];
int ir ,ic ;
int adelanteX[] = {1 ,0 ,-1 ,0};
int adelanteY[] = {0 ,1 ,0 ,-1};
int arribaAdelanteX[] = {1 ,1 ,-1 ,-1};
int arribaAdelanteY[] = {-1 ,1 ,1 ,-1};
int dir = 0;
int pps = 256 / resolucion;

      for(ic = 0; ic < matriz.length; ic++ )
      {
           matriz[0][ic] = 0;
           matriz[matriz.length - 1][ic] = 0;
           matriz[ic][0] = 0;
           matriz[ic][matriz.length - 1] = 0;
      }

      ir = 1;
      ic = 1;
      for(int pr=0; pr < patron.length; pr++)
      {
         for(int pc=0; pc < patron[0].length; pc++)
         {
              matriz[ir][ic] = patron[pr][pc];
//              System.out.print(""+matriz[ir][ic]+" ");
              ic++;
         }
         ir++;
         ic = 1;
//         System.out.println();
      }
//System.out.println("copie");

      // BUSCA EL PRIMER PIXEL NEGRO
int esta = 0;
int idc = 0, idr = 0; // �ndices de columna y renglon

      for(ir =0; ir < matriz.length && esta == 0; ir++)
      {
         for(ic=0; ic < matriz[0].length && esta == 0; ic++)
         {
//System.out.println(" ir="+ir+" "+" ic="+ic+" "+matriz[ir][ic]+" ");

            if( matriz[ir][ic] == 1 )
            {
                esta = 1;
                idc = ic;
                idr = ir;
            }
         }
//         System.out.println();
      }

//System.out.println("empiezo en "+idr+" "+idc);

StringBuffer numero = new StringBuffer();
int ultimo = 0;

int lax[] = new int[1000];
int lay[] = new int[1000];
int ula = 0;



      numero.append("1");
      dir = 0;
      ic = idc;
      ir = idr;
      raizX = (idc - 1) * pps;
      raizY = idr * pps;

      graficaUno(3);


      do
      {
//
//          System.out.println("ITERACION r="+idr+" c="+idc+" dir="+dir);
int regla = 0;
          // REGLA 1
          if( matriz[idr + adelanteY[dir] ][idc + adelanteX[dir] ] == 0 &&
            matriz[idr + arribaAdelanteY[dir] ][idc + arribaAdelanteX[dir] ] == 0 )
          {
              regla = 1;
              numero.append("1");

              graficaUno(dir);

              dir = (dir + 1) % 4;
//              System.out.println("Regla 1  r="+idr+" c="+idc+" dir="+dir);


          }
else
          // REGLA 2
          if( matriz[idr + adelanteY[dir]][idc + adelanteX[dir]] == 1 &&
            matriz[idr + arribaAdelanteY[dir]][idc + arribaAdelanteX[dir]] == 0 )
          {
              regla = 2;
              numero.append("2");

              graficaDos(dir);

              idc = idc + adelanteX[dir];
              idr = idr + adelanteY[dir];
//              System.out.println("Regla 2  r="+idr+" c="+idc+" dir="+dir);
          }
else
          // REGLA 3
          if( matriz[idr + arribaAdelanteY[dir]][idc + arribaAdelanteX[dir]] == 1 )
          {
              regla = 3;
              numero.append("3");

              graficaTres(dir);

              idr = idr + arribaAdelanteY[dir];
              idc = idc + arribaAdelanteX[dir];

              dir = (dir + 3) % 4;
//              System.out.println("Regla 3  r="+idr+" c="+idc+" dir="+dir);

          }
          /*
      int siEsta = 0;
          for( int pp = 0; pp < ula && siEsta == 0; pp++ )
          {
              if( idc == lax[pp] && idr == lay[pp] )
                  siEsta = 1;
          }
          if( siEsta == 0 )
          {
              lax[ula] = idc;
              lay[ula] = idr;
              ula++;
          }
          else
          {
               if( ultimo == 1 )
                    ultimo = 2;
               else
               if( regla == 2 )
                   ultimo = 2;
               else
                   ultimo++;
          }
          */
          if( idc == ic && idr == ir )
            ultimo++;

      } while( ultimo != 2 );
      tNumeroForma.setText(numero.toString() );
      return numero.toString();
}

void graficaUno(int dir)
{
int adelanteX[] = {1 ,0 ,-1 ,0};
int adelanteY[] = {0 ,1 ,0 ,-1};
int adelanteDerX[] = {1 ,-1 ,-1 ,1};
int adelanteDerY[] = {1 ,1 ,-1 ,-1};

int arribaAdelanteX[] = {1 ,1 ,-1 ,-1};
int arribaAdelanteY[] = {-1 ,1 ,1 ,-1};

int pps = 256 / resolucion;
Graphics gr = digital.getGraphics();

//System.out.println("1entra raiz "+raizX+" "+raizY+"  ->"+dir);

    Graphics2D g2 = (Graphics2D) gr;
        g2.setStroke(new BasicStroke(4));

    gr.setColor(Color.red);
    //gr.drawLine((ic - 1)*pps ,ir*pps,(ic - 1)*pps ,(ir - 1)*pps);
    gr.drawLine(raizX ,raizY, raizX + adelanteX[dir] * pps ,raizY + adelanteY[dir] * pps);


    //gr.drawLine((ic - 1)*pps,(ir - 1)*pps,ic*pps,(ir - 1)*pps);
    gr.drawLine(raizX + adelanteX[dir] * pps ,raizY + adelanteY[dir] * pps
        ,raizX + adelanteDerX[dir] * pps ,raizY + adelanteDerY[dir] * pps);

    gr.setColor(Color.orange);
    gr.fillOval(raizX - 3 ,raizY - 3,6,6);

    raizX += adelanteX[dir] * pps;
    raizY += adelanteY[dir] * pps;
//System.out.println("1sale raiz "+raizX+" "+raizY);

}

void graficaDos(int dir)
{
int adelanteX[] = {1 ,0 ,-1 ,0};
int adelanteY[] = {0 ,1 ,0 ,-1};
int adelanteDerX[] = {1 ,-1 ,-1 ,1};
int adelanteDerY[] = {1 ,1 ,-1 ,-1};

int arribaAdelanteX[] = {1 ,1 ,-1 ,-1};
int arribaAdelanteY[] = {-1 ,1 ,1 ,-1};

int pps = 256 / resolucion;
Graphics gr = digital.getGraphics();

//System.out.println("2entra raiz "+raizX+" "+raizY+"  ->"+dir);

    Graphics2D g2 = (Graphics2D) gr;
        g2.setStroke(new BasicStroke(4));

    gr.setColor(Color.blue);
    //gr.drawLine((ic - 1)*pps ,ir*pps,(ic - 1)*pps ,(ir - 1)*pps);
    gr.drawLine(raizX ,raizY, raizX + 2 * adelanteX[dir] * pps ,raizY + 2 * adelanteY[dir] * pps);


    gr.setColor(Color.orange);
    gr.fillOval(raizX - 3 ,raizY - 3,6,6);

    raizX += adelanteX[dir] * pps;
    raizY += adelanteY[dir] * pps;
  //  System.out.println("2sale raiz "+raizX+" "+raizY);
}


void graficaTres(int dir)
{
int adelanteX[] = {1 ,0 ,-1 ,0};
int adelanteY[] = {0 ,1 ,0 ,-1};
int adelanteIzqX[] = {1  ,1 ,-1 ,-1};
int adelanteIzqY[] = {-1 ,1 ,1 ,-1};

int arribaAdelanteX[] = {1 ,1 ,-1 ,-1};
int arribaAdelanteY[] = {-1 ,1 ,1 ,-1};

int pps = 256 / resolucion;
Graphics gr = digital.getGraphics();

//System.out.println("3entra raiz "+raizX+" "+raizY+"  ->"+dir);

    Graphics2D g2 = (Graphics2D) gr;
        g2.setStroke(new BasicStroke(4));

    gr.setColor(Color.green);
    //gr.drawLine((ic - 1)*pps ,ir*pps,(ic - 1)*pps ,(ir - 1)*pps);
    gr.drawLine(raizX ,raizY, raizX + adelanteX[dir] * pps ,raizY + adelanteY[dir] * pps);


    //gr.drawLine((ic - 1)*pps,(ir - 1)*pps,ic*pps,(ir - 1)*pps);
    gr.drawLine(raizX + adelanteX[dir] * pps ,raizY + adelanteY[dir] * pps
        ,raizX + adelanteIzqX[dir] * pps ,raizY + adelanteIzqY[dir] * pps);

    gr.setColor(Color.orange);
    gr.fillOval(raizX - 3 ,raizY - 3,6,6);

    raizX += adelanteX[dir] * pps;
    raizY += adelanteY[dir] * pps;
//System.out.println("3sale raiz "+raizX+" "+raizY);

}
//------------------------------------------------------------------------------------------------
    public Forma()
    {
    Container cont = getContentPane();
    Font fTitulo = new Font("Arial" ,Font.BOLD ,18);
    Font fNormal = new Font("Arial" ,Font.BOLD ,12);

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
        eTitulo.setBounds(centrar ,10 ,longitud ,40);



        bCarga.setFont(fNormal);
        cont.add(bCarga);
        bCarga.setBounds(20,50,100,30);

        eNomArch.setFont(fNormal);
        cont.add(eNomArch);
        eNomArch.setBounds(130,50,550,30);

        eResolucion.setFont(fNormal);
        cont.add(eResolucion);
        eResolucion.setBounds(20,95,150,30);

        tResolucion.setFont(fNormal);
        cont.add(tResolucion);
        tResolucion.setEditable(false);
        tResolucion.setBounds(90,95,30,30);

        bAumenta.setFont(fNormal);
        cont.add(bAumenta);
        bAumenta.setBounds(125,95,41,13);
        bAumenta.addActionListener(aAumenta);

        bReduce.setFont(fNormal);
        cont.add(bReduce);
        bReduce.setBounds(125,112,41,13);
        bReduce.addActionListener(aReduce);

        // COLOCA LOS PANELES EN LA PANTALLA
        panelScroll.setPreferredSize(new Dimension(400, 800));
        panelScroll.setMinimumSize(new Dimension(100, 100));
        panelScroll.setMaximumSize(new Dimension(1000, 1000));

        cont.add(panelScroll);
        panelScroll.setBounds(20,140,400,300);

        bReconoce.setFont(fNormal);
        cont.add(bReconoce);
        bReconoce.setBounds(500,95,150,30);

        bForma.setFont(fNormal);
        cont.add(bForma);
        bForma.setBounds(500,410,150,30);

        cont.add(digital);
        digital.addMouseListener(eventoRaton);
        digital.setBounds(430,140,260,260);

        tNumeroForma.setFont(fNormal);
        cont.add(tNumeroForma);
        tNumeroForma.setLineWrap(true);
        tNumeroForma.setBounds(20,460,550,60);

        tLongitud.setFont(fNormal);
        cont.add(tLongitud);
        tLongitud.setBounds(610,460,50,30);

        bGuarda.setFont(fNormal);
        cont.add(bGuarda);
        bGuarda.setBounds(670,460,80,30);


        bSalir.setFont(fNormal);
        cont.add(bSalir);
        bSalir.setBounds(610,500,140,30);

      // ASIGNA LAS ACCIONES

      bCarga.addActionListener(aCarga);
      bReconoce.addActionListener(aReconoce);
      bForma.addActionListener(aForma);
      bGuarda.addActionListener(aGuarda);
      bSalir.addActionListener(aSalir);

      // ACCION PARA CUANDO SE PIDE EL CIERRE DE LA VENTANA
      addWindowListener(new WindowAdapter()
      {
          public void windowClosing(WindowEvent e)
          {
              dispose();
          }
      });
  }

    // **********
    // tomaPatron
    // **********
    void tomaPatron(byte [][]_matriz ,int _ren ,int _col)
    {
    Graphics g2 = image.getGraphics();

    Cola cola = new Cola();
    int renVecino[] = {-1 ,-1 ,-1 ,0  ,0 ,1  ,1 ,1};
    int colVecino[] = {-1 ,0  ,1  ,-1 ,1 ,-1 ,0 ,1};
    int colMin = _col; int renMin = _ren;
    int colMax = _col; int renMax = _ren;

        // MARCA COMO PROCESADO EL PIXEL
        matriz[_ren][_col] = 2;
        cola.inserta(new Par(_ren ,_col));
        while( cola.vacia() == false )
        {
        Par par = (Par)cola.retira();

            // APLICA LA VECINDAD PARA ENCONTRAR EL PATRON
            for(int v = 0; v < renVecino.length; v++ )
            {
                if( par.ren + renVecino[v] < 0 || par.ren + renVecino[v] >= matriz.length )
                    continue;
                if( par.col + colVecino[v] < 0 || par.col + colVecino[v] >= matriz[0].length )
                    continue;

                // SI EXISTE UN VECINO == 1 LO ENCOLA
                if( matriz[par.ren + renVecino[v] ][par.col + colVecino[v] ] == 1 )
                {
                    cola.inserta(new Par(par.ren + renVecino[v] ,par.col + colVecino[v]) );
                    matriz[par.ren + renVecino[v] ][par.col + colVecino[v] ] = 2;
                    // RECALCULA LOS LIMITES DEL PATRON
                    if( (par.ren + renVecino[v]) < renMin )
                        renMin = par.ren + renVecino[v];
                    if( (par.ren + renVecino[v]) > renMax )
                        renMax = par.ren + renVecino[v];
                    if( (par.col + colVecino[v]) < colMin )
                        colMin = par.col + colVecino[v];
                    if( (par.col + colVecino[v]) > colMax )
                        colMax = par.col + colVecino[v];
                }
            }
        }

        // DETERMINA EL TAMA�O DE LA MATRIZ DEL PATRON OBTENIDO
        original = new byte[renMax - renMin + 1][colMax - colMin + 1];

        if( (renMax - renMin + 1) > 1 && (colMax - colMin + 1) > 1)
        {
            for(int iR = 0; iR < (renMax - renMin + 1); iR++)
                for(int iC = 0; iC < (colMax - colMin + 1); iC++)
                    original[iR][iC] = matriz[iR+renMin][iC+colMin];
        }
        else
        {
            // SI NO SE ENCONTR� UN PATR�N USA UN PIXEL
            original = new byte[1][1];
        }

        // COLOCA UN RECT�NGULO ROJO SOBRE LA IMAGEN DETECTADA
        g2.setColor(Color.red);
        g2.drawRect(colMin,renMin,colMax-colMin,renMax-renMin);
        Icon imageIcono = new ImageIcon( image );
        JLabel label = new JLabel( imageIcono );
        panelScroll.getViewport().add( label );

        // SE CALCULA EL �REA DE RECONOCIMIENTO CREANDO OTRA MATRIZ
        int factorC = colMax - colMin + 1;
        int factorR = renMax - renMin + 1;

        // DETERMINA EL FACTOR MAYOR
        int factor = factorC;
            if( factorR > factorC )
                factor = factorR;

        // GENERA UNA MATRIZ CUADRADA
        if( factor > 1 )
            referencia = new byte[factor][factor];
        else
            referencia = new byte[1][1];

        // PASA EL PATRON ORIGINAL A LA MATRIZ CUADRADA
        for(int iR = 0; iR < factorR; iR++)
        {
            for(int iC = 0; iC < factorC; iC++)
            {
                referencia[iR][iC] = (byte)
                    (matriz[renMin + iR][colMin + iC] == 2 ? 1 : 0);
            }
        }
    }

    public static void main(String args[])
    {

    Dimension tamano = Toolkit.getDefaultToolkit().getScreenSize();

    Forma ventanaPrincipal = new Forma();

        //sETexto.setSize((int)(tamano.width * 0.9), (int)(tamano.height * 0.9));
        ventanaPrincipal.setSize(800,600);
        ventanaPrincipal.setLocationRelativeTo(null);
        ventanaPrincipal.setVisible(true);

    }

}