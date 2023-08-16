package net.laenredadera.full.peliculas.gratis.de.terror.Utils;

import android.net.Uri;

import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmFavorites;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class markAsFavorites {

    private final Realm realm;

    public markAsFavorites() {
        this.realm = Realm.getDefaultInstance();
    }

    public void setAsFavorites() {

        RealmList<RealmFavorites> favorites = loadFavoritesFromRealm();
        realm.beginTransaction();
        for (RealmFavorites f : favorites) {
            RealmMovieData m = realm.where(RealmMovieData.class)
                    .equalTo("enabled",1)
                    .equalTo("link", f.getLink())
                    .findFirst();
            if (m != null) {
                m.setFavorite(true);
            }
        }
        realm.commitTransaction();
    }

    public RealmList<RealmFavorites> loadFavoritesFromRealm() {

        RealmList<RealmFavorites> FavVideos = new RealmList<RealmFavorites>();
        RealmResults<RealmFavorites> result;

        result = realm.where(RealmFavorites.class)
                .findAll();

        FavVideos.addAll(result.subList(0, result.size()));

        return FavVideos;

    }
    public void manageSetAsFavorite(RealmMovieData video){
        RealmFavorites fav = realm.where(RealmFavorites.class).equalTo("link",video.getLink()).findFirst();

        realm.beginTransaction();
        if (fav != null) {
            Uri l1 = Uri.parse(fav.getLink());
            Uri l2 = Uri.parse(video.getLink());

            if (l1.equals(l2)) {
                video.setFavorite(false);// Swap If video is in favorites
                fav.deleteFromRealm();

            } else {
                video.setFavorite(true); // Swap If video is in not favorites
                RealmFavorites favorite = new RealmFavorites(video.getLink());
                realm.copyToRealmOrUpdate(favorite);
            }

        } else {
            video.setFavorite(true);
            RealmFavorites favorite = new RealmFavorites(video.getLink());
            realm.copyToRealmOrUpdate(favorite);

        }
        realm.commitTransaction();

    }

    public int getFavoritesCount(){

        RealmList<RealmFavorites> result = loadFavoritesFromRealm();

        return result.size();

    }

}