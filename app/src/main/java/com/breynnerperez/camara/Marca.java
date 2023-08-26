package com.breynnerperez.camara;

public class Marca {
    // Tipo nombreVariable; =Valor;

    private int id;
    private String nombre;
    private String descripcion;
    private  double Precio;

    //METODOS - FUNCIONES - ACCIONES
    //Constructor vacio
    public Marca() {
    }

    @Override
    public String toString(){
        return id +" "+nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio){
        this.Precio = precio;
    }
    public double getPrecio(){
        return Precio;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;


    }
}
