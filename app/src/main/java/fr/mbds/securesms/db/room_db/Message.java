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
    private String author;

    @ColumnInfo(name = "msg")
    private String message;

    @ColumnInfo(name = "date")
    private String dateCreated;

    @ColumnInfo(name = "read")
    private boolean alreadyReturned;

    @ColumnInfo(name = "sender")
    private boolean currentUser;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getAlreadyReturned() {
        return alreadyReturned;
    }

    public void setAlreadyReturned(boolean alreadyReturned) {
        this.alreadyReturned = alreadyReturned;
    }

    public boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }
}
