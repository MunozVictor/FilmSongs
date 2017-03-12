package com.example.vicapps.filmsongs_victor;

/**
 * Created by 21542295 on 08/03/2017.
 */
public class Pelicula {

    String titulo;
    String cancion;


    public Pelicula(String titulo,String cancion) {
        this.cancion = cancion;
        this.titulo = titulo;
    }

    public String getCancion() {
        return cancion;
    }


    public String getTitulo() {
        return titulo;
    }


    @Override
    public String toString() {
        return titulo;
    }

}
