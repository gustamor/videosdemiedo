package net.laenredadera.full.peliculas.gratis.de.terror;

import com.google.gson.GsonBuilder;

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Deserializer.MovieDeserializer;
import net.laenredadera.full.peliculas.gratis.de.terror.Deserializer.TokenInterceptor;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    MyApp app = new MyApp();
    public final String BASE_URL = app.getAppContext().getString(R.string.strapiendpoint);
    private static Retrofit retrofit = null;
    static final TokenInterceptor interceptor=new TokenInterceptor();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

    public Retrofit getApi() {
        if (retrofit == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(RealmMovieData.class, new MovieDeserializer());
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .client(client)
                    .build();
        }

        return retrofit;
    }

}
