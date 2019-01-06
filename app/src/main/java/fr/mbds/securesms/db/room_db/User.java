package fr.mbds.securesms.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "username", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    int uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "picture")
    private String thumbnail;

    @ColumnInfo(name = "indexToPubKey")
    private String idPubKey = "SEND_PING";

    @ColumnInfo(name = "pubKey")
    private String pubKey;

    @ColumnInfo(name = "privateKey")
    private String privateKey;

    @ColumnInfo(name = "aesKey")
    private String aesKey;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPubKey() { return pubKey; }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivateKey() { return privateKey; }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAesKey() { return aesKey; }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
