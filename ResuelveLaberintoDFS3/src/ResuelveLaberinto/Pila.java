package ResuelveLaberinto;


class Nodo
{
Object dato;
Nodo sig;

    Nodo(Object algo)
    {
        dato = algo;
        sig = null;
    }
}


public class Pila
{
Nodo tope ;

    Pila()
    {
        tope = null;
    }

    void inserta(Object algo)// WRITE
    {
    Nodo nuevo = new Nodo(algo);

     if( tope == null )
     {
         tope = nuevo;
     }
     else
        {
            nuevo.sig = tope;
            tope = nuevo;
        }
    }

    Object retira() // READ
    {
        if( tope == null )
            return null;
    Object solicitado = tope.dato;

            tope = tope.sig;
        return solicitado;
    }

    int cupo()
    {
    int cuenta = 0;
    Nodo tmp = tope;

        if( tope == null )
            return 0;
        while( tmp != null )
        {
            cuenta++;
            tmp = tmp.sig;
        }
        return cuenta;
    }

    boolean vacia()
    {
        return tope == null ? true : false;
    }
}
