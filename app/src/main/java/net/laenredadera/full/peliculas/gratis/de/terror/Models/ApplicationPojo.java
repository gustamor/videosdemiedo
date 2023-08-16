package net.laenredadera.full.peliculas.gratis.de.terror.Models;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;

public class ApplicationPojo {
   
    @Expose
    private RealmList<RealmMovieData> peliculas;

    public ApplicationPojo(RealmList<RealmMovieData> peliculas) {
        this.peliculas = peliculas;
    }

    public RealmList<RealmMovieData> getPeliculas() {
        return this.peliculas;
    }

    public void setPeliculas(RealmList<RealmMovieData> peliculas) {
        this.peliculas = peliculas;
    }
}