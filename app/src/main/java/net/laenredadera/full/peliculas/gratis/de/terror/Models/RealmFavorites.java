package net.laenredadera.full.peliculas.gratis.de.terror.Models;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmFavorites extends RealmObject {

    @Expose
    @PrimaryKey
    private String link;
   /* @Expose
    private int enabled;*/

    public RealmFavorites(){
    }

    public RealmFavorites(String link) {
        this.link = link;
    }

   /* public RealmFavorites(String link, int enabled) {
        this.link = link;
        this.enabled = enabled;
    }
*/
    public String getLink() {
        return link;
    }

   /* public void setLink(String link) {
        this.link = link;
    }*/
  /*  public int getEnabled() { return enabled;  }

    public void setEnabled(int enabled) { this.enabled = enabled; }*/
}
