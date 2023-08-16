package net.laenredadera.full.peliculas.gratis.de.terror.Deserializer;

import androidx.annotation.NonNull;

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    String jwt;
    MyApp app = new MyApp();
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (app.getJwt() != null && !app.getJwt().equals(""))  {
            jwt = app.getJwt();
        } else {
            jwt = app.getAppContext().getString(R.string.jwt);
        }
        //rewrite the request to add bearer token
        Request newRequest = chain.request().newBuilder()
                .header("Content-Type", "application/json")
                .build();
        return chain.proceed(newRequest);
    }
}
