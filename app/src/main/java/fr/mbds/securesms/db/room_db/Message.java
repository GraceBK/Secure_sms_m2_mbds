package fr.mbds.securesms.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "username", unique = true)})
public class Message {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "msg")
    private String message;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
