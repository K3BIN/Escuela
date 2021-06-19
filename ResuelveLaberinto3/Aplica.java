/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIShapenumber;

class Regla
{
String antes;
String despues;
int avance ;

    Regla(String _antes ,String _despues ,int _avance)
    {
        antes = _antes;
        despues = _despues;
        avance = _avance;
    }
    Regla()
    {
        antes = "";
        despues = "";
        avance = 0;
    }

}

class Estado
{
int nodo;
int padre;

String antes;
int regla;
int posicion;
String despues;
}

