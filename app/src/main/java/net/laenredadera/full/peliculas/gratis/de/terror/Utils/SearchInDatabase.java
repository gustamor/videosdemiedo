package net.laenredadera.full.peliculas.gratis.de.terror.Utils;

import android.net.Uri;
import android.util.Log;

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmFavorites;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchInDatabase {
    private final Realm db;
    private String orderedBy;
    private int size = 0;
    MyApp app = new MyApp();
    public SearchInDatabase() {
        this.db = Realm.getDefaultInstance();
    }

    public SearchInDatabase(Realm realm) {
        this.db = realm;
    }

    public RealmList<RealmMovieData> getSearchResult(String searchfor) {

        RealmList<RealmMovieData> RVideos = new RealmList<RealmMovieData>();
        RealmList<RealmMovieData> FinalVideos = new RealmList<RealmMovieData>();

        RealmResults<RealmMovieData> resultTitle;
        RealmResults<RealmMovieData> resultYear;
        RealmResults<RealmMovieData> resultPlot;


        ArrayList<String> desires = new ArrayList<>();
        Collections.addAll(desires, searchfor.split(" "));

       /* // Search single words
        for (String word: desires) {
            try {
                resultTitle = db.where(RealmMovieData.class)
                        .contains("title", word, Case.INSENSITIVE)
                        .equalTo("enabled",1)
                        .findAll();
                resultPlot = db.where(RealmMovieData.class)
                        .contains("plot", word, Case.INSENSITIVE)
                        .equalTo("enabled",1)
                        .findAll();
                RVideos.addAll(resultTitle.subList(0, resultTitle.size()));
                RVideos.addAll(resultPlot.subList(0, resultPlot.size()));

            } catch (Error err) {
                Log.e("SearchInDatabaseClass: ", err.toString());
            }
        }*/

        // Search the whole text query
        resultTitle = db.where(RealmMovieData.class)
                .contains("title", searchfor, Case.INSENSITIVE)
                .equalTo("enabled",1)
                .findAll();
        resultPlot = db.where(RealmMovieData.class)
                .contains("plot", searchfor, Case.INSENSITIVE)
                .equalTo("enabled",1)
                .findAll();

        RVideos.addAll(resultTitle.subList(0, resultTitle.size()));
        RVideos.addAll(resultPlot.subList(0, resultPlot.size()));


        for (RealmMovieData o: RVideos) {
            if (!FinalVideos.contains(o)) {
                FinalVideos.add(o);
            }

        }
        return FinalVideos;

    }

    // Function: loadVideosFromRealm
    // Produces: a Reamlist<Moviedata>

    public RealmList<RealmMovieData> getArrayListFromRealm() {

        RealmList<RealmMovieData> videos = new RealmList<RealmMovieData>();
        RealmResults<RealmMovieData> result;
        result = this.getResultsFromRealm();
        videos.addAll(result.subList(0, result.size()));
        return videos;

    }
    public RealmResults<RealmMovieData> getResultsFromRealm() {

        Sort sortType = Sort.ASCENDING;
        orderedBy = app.getAdapterOrdered();
        if (orderedBy == null) orderedBy = "desc";
        switch (orderedBy){
            case "asc":
                sortType = Sort.ASCENDING;
                break;
            case "desc":
                sortType = Sort.DESCENDING;
                break;
        }

        RealmList<RealmMovieData> videos = new RealmList<RealmMovieData>();
        RealmResults<RealmMovieData> result;

        result = this.db.where(RealmMovieData.class)
                .equalTo("enabled",1)
                .findAll()
                .sort("uploadedDate", sortType);

        if (result != null) size = result.size();
        return result;

    }
    public RealmList<RealmMovieData> getFavoritesListFromRealm() {

        Sort sortType = Sort.ASCENDING;
        orderedBy = app.getAdapterOrdered();
        if (orderedBy == null) orderedBy = "desc";
        switch (orderedBy){
            case "asc":
                sortType = Sort.ASCENDING;
                break;
            case "desc":
                sortType = Sort.DESCENDING;
                break;
        }

        RealmList<RealmMovieData> videos = new RealmList<RealmMovieData>();
        RealmResults<RealmMovieData> result;

        result = this.db.where(RealmMovieData.class)
                .equalTo("isFavorite",true)
                .equalTo("enabled",1)
                .findAll()
                .sort("uploadedDate",  sortType);
        if (result.size() > 0) videos.addAll(result.subList(0, result.size()));

        return videos;

    }

    public int getSize() {
        return this.size;
    }
    private void manageSetAsFavorite(RealmMovieData video){
        RealmFavorites fav = db.where(RealmFavorites.class).equalTo("link",video.getLink()).findFirst();

        db.beginTransaction();
        if (fav != null) {

            Uri l1 = Uri.parse(fav.getLink());
            Uri l2 = Uri.parse(video.getLink());

            if (l1.equals(l2)) {
                video.setFavorite(false);// Swap If video is in favorites
                fav.deleteFromRealm();

            } else {
                video.setFavorite(true); // Swap If video is not in favorites
                RealmFavorites favorite = new RealmFavorites(video.getLink());
                db.copyToRealmOrUpdate(favorite);
            }

        } else {
            video.setFavorite(true);
            RealmFavorites favorite = new RealmFavorites(video.getLink());
            db.copyToRealmOrUpdate(favorite);

        }
        db.commitTransaction();
    }

}