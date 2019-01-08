package fr.mbds.securesms.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

// USER(uid , username , status , publicKey , privateKey , aesKey)

@Entity(indices = {@Index(value = "username", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    int uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "picture")
    private String thumbnail;

    @ColumnInfo(name = "status")
    private String status = "SEND_PING";

    @ColumnInfo(name = "publicKey")
    private String publicKey;

    @ColumnInfo(name = "privateKey")
    private String privateKey;

    @ColumnInfo(name = "aesKey")
    private String aesKey;

    @Override
    public String toString() {
        return "[\n" + getUsername() + " | " + getStatus() + "\n\n" +
                "PUB_KEY = " + getPublicKey() + "\n\n" +
                "PRI_KEY = " + getPrivateKey() + "\n\n" +
                "SEC_KEY = " + getAesKey();
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicKey() { return publicKey; }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
