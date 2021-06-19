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

public class Cola
{
Nodo frente ;
Nodo ultimo ;

    Cola()
    {
        frente = null;
        ultimo = null;
    }

    void inserta(Object algo)// WRITE
    {
    Nodo nuevo = new Nodo(algo);

     if( frente == null )
     {
          ultimo = nuevo;
          frente = nuevo;
        }
        else
        {
            ultimo.sig = nuevo;
            ultimo = nuevo;
        }
    }

    Object retira() // READ
    {
        if( frente == null )
            return null;
    Object solicitado = frente.dato;
        if( frente == ultimo )
        {
            frente = null;
            ultimo = null;
        }
        else
        {
            frente = frente.sig;
        }
        return solicitado;
    }

    int cupo()
    {
    int cuenta = 0;
    Nodo tmp = frente;

        if( frente == null )
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
        return frente == null ? true : false;
    }
}
