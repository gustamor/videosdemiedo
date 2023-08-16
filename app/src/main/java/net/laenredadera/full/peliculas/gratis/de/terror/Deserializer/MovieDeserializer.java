package net.laenredadera.full.peliculas.gratis.de.terror.Deserializer;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;

import java.lang.reflect.Type;

public class MovieDeserializer implements JsonDeserializer<RealmMovieData> {

    @Override
    public RealmMovieData deserialize(JsonElement videoList, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RealmMovieData movie = new RealmMovieData();
        movie.setPoster(videoList.getAsJsonObject().get("backdrop").getAsString());
        movie.setIcon(videoList.getAsJsonObject().get("icon").getAsString());
        int id = videoList.getAsJsonObject().get("id").getAsInt();
        movie.setId(String.valueOf(id));
        movie.setTitle(videoList.getAsJsonObject().get("titulo").getAsString());
        if (videoList.getAsJsonObject().get("subtitle") != null) {
            movie.setSubtitle(videoList.getAsJsonObject().get("subtitle").getAsString());
        } else movie.setSubtitle(" ");
        movie.setYear(videoList.getAsJsonObject().get("year").getAsInt());
        movie.setPlot(videoList.getAsJsonObject().get("plot").getAsString());
        movie.setRuntime(videoList.getAsJsonObject().get("runtime").getAsString());
        movie.setLink(videoList.getAsJsonObject().get("video_url").getAsString());
        boolean enabled = videoList.getAsJsonObject().get("enabled").getAsBoolean();
        movie.setUploadedDate(videoList.getAsJsonObject().get("uploaded").getAsLong());
        movie.setEnabled(enabled ? 1 : 0);
        return movie;

    }

}
