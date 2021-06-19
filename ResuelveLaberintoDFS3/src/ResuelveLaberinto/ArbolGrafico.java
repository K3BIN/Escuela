package ResuelveLaberinto;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

class ArbolGrafico extends JFrame {
    
    Canvas lienzo = new Canvas();
    ScrollPane scrollLienzo = new ScrollPane();
    JButton bGrafica = new JButton("Grafica");
    JButton bRegresa = new JButton("Regresa");

    int XPMax = 1366;
    int YPMaxIni = 768;

    int nivelMaximo;
    ArrayList visitados = new ArrayList<Estado>();
    ArrayList elPlan = new ArrayList<Estado>();

    ActionListener aGrafica = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            dibujaArbol();
        }
    };

    ActionListener aRegresa = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            dispose();
        }
    };

    public ArbolGrafico(ArrayList visitados, int nivelMaximo, ArrayList elPlan) {
        Container cont = getContentPane();
        cont.setLayout(null); // DISTRIBUCIÓN DEL CONTENEDOR LIBRE
        cont.setBackground(Color.white); // FONDO BLANCO

        this.setSize(XPMax, YPMaxIni);
        this.setVisible(true);

        this.nivelMaximo = nivelMaximo;
        this.visitados = visitados;
        this.elPlan = elPlan;

        cont.add(bGrafica);
        bGrafica.setBounds(XPMax / 2 - 125, 0, 100, 30);
        bGrafica.setVisible(true);

        cont.add(bRegresa);
        bRegresa.setBounds(XPMax / 2 + 25, 0, 100, 30);
        bRegresa.setVisible(true);

        
        lienzo.setBounds(0,0,1920,2048);
        scrollLienzo.add(lienzo);
        cont.add(scrollLienzo);
        scrollLienzo.setBounds(0, 40, XPMax, YPMaxIni - 40);
        scrollLienzo.setVisible(true);

        // ACCION PARA CUANDO SE PIDE EL CIERRE DE LA VENTANA
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0); // dispose();
            }
        });
        // he colocado los componentes gráficos en la ventana

        // ASIGNA LAS ACCIONES A CADA BOTÓN
        bGrafica.addActionListener(aGrafica);
        bRegresa.addActionListener(aRegresa);
    }

    public void dibujaArbol() {
        Graphics g = lienzo.getGraphics();
        lienzo.setBackground(Color.white);
        int n = visitados.size();
        Font tipoLetra = new Font("Arial", Font.BOLD, 12);

        g.setColor(Color.blue);
        g.setFont(tipoLetra);

        for (int iNivel = 0; iNivel < nivelMaximo; iNivel++) {
            // CONTAMOS EL NÚMERO DE NODOS EN CADA NIVEL
            int cuenta = 0;
            for (int i = 0; i < n; i++) {
                Estado estado = (Estado) visitados.get(i);
                if (estado.nivel == iNivel) {
                    cuenta++;
                }
            }

            // DISTRIBUIMOS LOS NODOS DEL NIVEL ACTUAL (iNivel)
            int espacio = XPMax / (cuenta + 1); // ESPACIO QUE LE TOCA A CADA NODO EN PIXELES
            int x = espacio;

            // SE DESPLIEGA CADA NODO DEL NIVEL ACTUAL           
            for (int i = 0; i < n; i++) {
                Estado estado = (Estado) visitados.get(i);
                if (estado.nivel == iNivel) {
                    estado.x = x + 10;
                    estado.y = 20 + 16 * iNivel;
                    //g.drawString(""+estado.id, x, 50 + 18*iNivel);
                    g.drawString("(" + estado.ren + "," + estado.col + ") "+estado.id, x, estado.y);
                    x += espacio;
                }
                visitados.set(i, estado);
            }
        }

        // SE DIBUJAN LOS ARCOS INICIANDO EN EL NIVEL 1 BUSCANDO EL PADRE DE CADA NODO
        for (int i = 0; i < n; i++) {
            Estado estado = (Estado) visitados.get(i);
            int p = posicionEstado(estado.padre, visitados);
            if (p == -1) {
                continue;
            }
            Estado padre = (Estado) visitados.get(p);
            g.drawLine(estado.x, estado.y + 1, padre.x, padre.y);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }

        // SE DIBUJAN LOS ARCOS DEL PLAN
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(3));

        int longitudPlan = elPlan.size();
        for (int i = 0; i < longitudPlan; i++) {
            Estado estado = (Estado) elPlan.get(i);
            int p = posicionEstado(estado.padre, elPlan);
            if (p == -1) {
                continue;
            }
            Estado padre = (Estado) elPlan.get(p);
            g2.drawLine(estado.x, estado.y, padre.x, padre.y);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
            }
        }
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
}
