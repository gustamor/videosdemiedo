package net.laenredadera.full.peliculas.gratis.de.terror.Models;


import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmMovieData extends RealmObject {

    @Index
    private boolean isBeingSaved;
    @Index
    private boolean isFavorite;
    @Expose
    private String id;
    @Expose
    private String title;
    @Expose
    private String subtitle;
    @Expose
    private int year;
    @Expose
    private String runtime;
    @Expose
    private String plot;
    @Expose
    private String poster;
    @Expose
    private String icon;

    @Required
    @PrimaryKey
    private String link;
    @Expose
    private String localIconUri;
    @Expose
    private String localPosterUri;
    @Expose
    private String source;
    @Expose
    private int enabled;
    @Expose
    private long uploadedDate;
    @Expose
    private int adapterPosition;


    /**
     * No args constructor for use in serialization
     *
     */
    public RealmMovieData() {
    }


    /**
     *  @param id
     * @param title
     * @param subtitle
     * @param runtime
     * @param plot
     * @param icon
     * @param poster;
     * @param icon;
     * @param  language;
     * @param  link;
     * @param  localIconUri;
     * @param localPosterUri;
     *
     *
     *
     */
    public RealmMovieData(String id, String title, String subtitle, String runtime, String plot, String poster, String icon, String language, String link, String localIconUri, String localPosterUri) {
        // this.id_ = MyApplication.VideoID.incrementAndGet();
        this.isBeingSaved = false;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.runtime = runtime;
        this.plot = plot;
        this.poster = poster;
        this.icon = icon;
        this.link = link;
        this.localIconUri = localIconUri;
        this.localPosterUri = localPosterUri;
    }


    public void setIsBeingSaved(boolean b){
        this.isBeingSaved = b;
    }

    public boolean getIsBeingSaved(){
        return isBeingSaved;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {

        return subtitle;
    }

    public void setSubtitle(String subtitle) {

        if (subtitle != null) this.subtitle = subtitle;
        else this.subtitle = " ";
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getYear() { return String.valueOf(year); }
    public void setYear(int year) {this.year = year;}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }



    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getLocalIconUri() {
        return localIconUri;
    }

    public void setLocalIconUri(String localIconUri) {
        this.localIconUri = localIconUri;
    }

    public String getLocalPosterUri() {
        return localPosterUri;
    }

    public void setLocalPosterUri(String localPosterUri) {
        this.localPosterUri = localPosterUri;
    }
    public long getUploadedDate() {

        return uploadedDate;

    }

    public void setUploadedDate(long uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }
    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }


    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}
