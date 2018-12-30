package fr.mbds.securesms.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "username", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "mail")
    private String mail;

    @ColumnInfo(name = "picture")
    private String thumbnail;

    @ColumnInfo(name = "indexToPubKey")
    private String idPubKey = "VIDE";


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIdPubKey() {
        return idPubKey;
    }

    public void setIdPubKey(String idPubKey) {
        this.idPubKey = idPubKey;
    }
}
