package net.laenredadera.full.peliculas.gratis.de.terror.Interfaces;

import net.laenredadera.full.peliculas.gratis.de.terror.Models.ApplicationPojo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieDataService {
    @GET("/aplicaciones/1/")
    Call<ApplicationPojo> getVideos();
}
