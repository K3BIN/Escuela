package ResuelveLaberinto;

class Estado {

    int accion;
    int id;
    int padre;
    int ren;
    int col;
    
    int nivel;
    int x, y;
    
    static int contadorId = 0;

    Estado(int id, int padre, int ren, int col, int nivel) {
        accion = -1;
        this.id = id;
        this.padre = padre;
        this.ren = ren;
        this.col = col;
        
        this.nivel = nivel;
        
        x = -1;
        y = -1;
    }
}
