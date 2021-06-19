package GUIShapenumber;

public class Escala
{
// MATRIZ ORIGINAL
byte [][]original ;
int maxRO ,maxCO ;

// MATRIZ CON EL PATRON RESULTADO DEL ESCALAMIENTO
double [][]patron ;
int maxRP ,maxCP ;

// FACTORES DE ESCALAMIENTO
double factorR ,factorC ;

    Escala(byte [][]_original)
    {
//        System.out.println("ENTRO AL CONSTRUCTOR");
        original = _original;
//        System.out.println("ORIGINAL MATRIX SIZE="+original.length+" "+ original[0].length);

        // DESPLIEGA LA IMAGEN ORIGINAL
       /*
        for (int r = 0; r < original.length; r++)
        {
            for (int c = 0; c < original[0].length; c++)
                System.out.print(""+original[r][c]+" ");
            System.out.println();
        }
        */
//        System.out.println("SALGO DEL CONSTRUCTOR");
    }

    // SE PROPORCIONA EL TAMA�O DE LA MATRIZ patron Y SE HACE ESCALAMIENTO SENCILLO
    byte [][]escalaBinario(int _maxRP ,int _maxCP)
    {
        // TOMA LAS DIMENSIONES DEL PATRON
        maxRP = _maxRP;
        maxCP = _maxCP;
    byte [][]patronBin = new byte[maxRP][maxCP];
        patron = new double[maxRP][maxCP];

        // TOMA LAS DIMENSIONES DE LA MATRIZ ORIGINAL
        maxRO = original.length;
        maxCO = original[0].length;

        // INICIALIZA EL PATRON CON CEROS
        for (int rP = 0; rP < maxRP; rP++)
            for (int cP = 0; cP < maxCP; cP++)
                patron[rP][cP] = 0.0;

        factorR = (double)maxRO / (double)maxRP;
        factorC = (double)maxCO / (double)maxCP;

    double factor = factorR;
        if( factorC > factorR )
            factor = factorC;


// SI FACTOR R Y FACTOR C SON MENORES A UNO ENTONCES SE PASA LA IMAGEN ORIGINAL AL PATRON EN ESCALA 1 A 1
        if( factorR <= 1 && factorC <= 1 )
            System.out.println("Se pasa la original al patron directamente");
        else
        {
            if( factorR <= 1 ) // LA IMAGEN CABE EN LOS RENGLONES
            {
                factorR = 1;
                maxRP = maxRO;
            }

            if( factorC <= 1 ) // LA IMAGEN CABE EN LAS COLUMNAS
            {
                factorC = 1;
                maxCP = maxCO;
            }

//            System.out.println("\nCALCULOS DE columna A REALIZAR ");
        double limInf = 0;
            for( int c = 0; c < _maxCP; c++ )
            {
//                System.out.printf("%d(%.2f-%.2f) ",c,limInf,limInf+factorC);
                limInf += factorC;
            }
//            System.out.println("\n\nCALCULOS DE renglon A REALIZAR ");
            limInf = 0;
            for( int r = 0; r < _maxRP; r++ )
            {
//                System.out.printf("%d(%.2f-%.2f) ",r,limInf,limInf+factorR);
                limInf += factorR;
            }
            escala();
        }

        // ************************************************
        // SE PROCESA LA IMAGEN PARA CONVERTIRLA EN BINARIO
        // ************************************************
        for (int rP = 0; rP < _maxRP; rP++)
            for (int cP = 0; cP < _maxCP; cP++)
                patronBin[rP][cP] = (byte)(patron[rP][cP] > 0.3 ? 1 : 0);
        return patronBin;
    }

    private void escala()
    {
    double masa = 0;
    double
        limInfR = 0 // EL INCREMENTO EN R ES factorR
        ,limInfC = 0 // EL INCREMENTO EN C ES factorC
        ,limSupR = 0
        ,limSupC = 0;

    // VARIABLES PARA ESCALAR RENGLONES
    int enteroR = 0;
    double fraccSup = 0, fraccInf = 0;

    // VARIABLES PARA ESCALAR COLUMNAS
    int enteroC = 0;
    double fraccIzq = 0, fraccDer = 0;

    // RECORRE LA IMAGEN original PARA OBTENER LOS PIXELES DEL patron
    int rP = 0, cP = 0; // COORDENADAS DEL PATRON

System.out.println();

    // POR CADA RENGLON DEL PATRON
    for(rP = 0; rP < maxRP; rP++ )
    {
        limInfR = (double)rP * factorR;
        limSupR = limInfR + factorR;

//System.out.printf("R %d(%.2f-%.2f)\n",rP,limInfR,limSupR);

        // CALCULA LOS PARAMETROS DE RENGLON
        fraccSup = techo(limInfR) - limInfR;
        enteroR = (int)limSupR - techo(limInfR);
        fraccInf = limSupR - (int)limSupR;

//System.out.printf("R %d(%5.2f,%d,%5.2f)\n",rP,fraccSup,enteroR,fraccInf);

        // POR CADA COLUMNA DEL PATRON
        for(cP = 0; cP < maxCP; cP++ )
        {
            limInfC = (double)cP * factorC;
            limSupC = limInfC + factorC;

            // CALCULA LOS PARAMETROS DE COLUMNA
            fraccIzq = techo(limInfC) - limInfC;
            enteroC = (int)limSupC - techo(limInfC);
            fraccDer = limSupC - (int)limSupC;


//    System.out.printf("    C %d(%.2f-%.2f)\n",cP,limInfC,limSupC);
//    System.out.printf("    C %d(%5.2f,%d,%5.2f)\n",cP,fraccIzq,enteroC,fraccDer);



            // CALCULANDO LA PONDERACION DEL RENGLON rP COLUMNA cP
           masa = 0;
        int rO = (int)limInfR;  // RENGLON DE LA IMAGEN original
        int cO = (int)limInfC;  // COLUMNA DE LA IMAGEN original
        int rOI = rO;
        int cOI = cO;

            // SE CALCULA LA LINEA si SUPERIOR > 0
            if( fraccSup > 0 )
            {
                // CALCULA 1 2 3

                // CALCULA EL AREA 1
                if( fraccIzq > 0 )
                {
                    patron[rP][cP] += (double)original[rO][cO] * fraccIzq * fraccSup;
                    masa += fraccIzq * fraccSup;
                    cO++;
                }

                // CALCULA LAS AREAS 2
                for( int cAux = 0; cAux < enteroC; cAux++)
                {
                    patron[rP][cP] += (double)original[rO][cO] * fraccSup;
                    masa += fraccSup;
                    cO++;
                }

                // CALCULA LAS AREAS 3
                if( fraccDer > 0 )
                {
                    if( rO < maxRO && cO < maxCO )
                    {
                        patron[rP][cP] += (double)original[rO][cO] * fraccDer * fraccSup;
                    masa += fraccDer * fraccSup;
                    }
                } // TERMINA EL CALCULO DE 1 2 3

                // AVANZA AL SIGUIENTE RENGLON EN SU COLUMNA INICIAL
                rO++;
                cO = cOI;
            }

            // CALCULAR LAS AREAS 4 5 6
            for( int rAux = 0; rAux < enteroR; rAux++)
            {
                // CALCULA EL AREA 4
                if( fraccIzq > 0 )
                {
                    patron[rP][cP] += (double)original[rO][cO] * fraccIzq;
                    masa += fraccIzq;
                    cO++;
                }

                // CALCULA LAS AREAS 5
                for( int cAux = 0; cAux < enteroC; cAux++)
                {
                    patron[rP][cP] += (double)original[rO][cO];
                    masa += 1;
                    cO++;
                }

                // CALCULA EL AREA 6
                if( fraccDer > 0 )
                {
                    if( rO < maxRO && cO < maxCO )
                    {
                    patron[rP][cP] += (double)original[rO][cO] * fraccDer;
                    masa += fraccDer;
                    }
                }

                // AVANZA AL SIGUIENTE RENGLON EN SU COLUMNA INICIAL
                rO++;
                cO = cOI;
            }

            // SE CALCULA LA LINEA si INFERIOR > 0
            if( fraccInf > 0 )
            {
                // CALCULA 7 8 9

                // CALCULA EL AREA 7
                if( fraccIzq > 0 )
                {
                    if( rO < maxRO && cO < maxCO )
                    {
                        patron[rP][cP] += (double)original[rO][cO] * fraccIzq * fraccInf;
                        masa += fraccIzq * fraccInf;
                    }
                    cO++;
                }

                // CALCULA LAS AREAS 8
                for( int cAux = 0; cAux < enteroC; cAux++)
                {
                    if( rO < maxRO && cO < maxCO )
                    {
                       patron[rP][cP] += (double)original[rO][cO] * fraccInf;
                        masa += fraccInf;
                    }
                    cO++;
                }

                // CALCULA EL AREA 9
                if( fraccDer > 0 )
                {
                    if( rO < maxRO && cO < maxCO )
                    {
                        patron[rP][cP] += (double)original[rO][cO] * fraccDer * fraccInf;
                        masa += fraccDer * fraccInf;
                    }
                }

                // TERMINA EL CALCULO DE 7 8 9
            }

                        // SE NORMALIZA DIVIDIENDO EL AREA ENTRE LA MASA
            if( masa <= 0 )
            {
                System.out.println("ERROR");
                System.exit(-1);
            }
            patron[rP][cP] = patron[rP][cP] / masa;
        }
    }
  }


    int techo(double _variable)
    {
    int auxiliar;

        auxiliar = (int)_variable;
        if( (_variable - (double)auxiliar) > 0 )
           auxiliar++;
        return auxiliar;
    }
/*
    void pintaPatron()
    {
        System.out.println("DESPLEGANDO PATRON");
        for (int r2P = 0; r2P < maxRP; r2P++)
        {
            for (int c2P = 0; c2P < maxCP; c2P++)
            {
                System.out.printf("%5.2f ",patron[r2P][c2P]);
            }
            System.out.println();
        }
    }
    */
}