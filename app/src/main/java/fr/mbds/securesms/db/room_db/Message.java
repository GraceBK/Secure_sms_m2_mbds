package fr.mbds.securesms.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

// @Entity(foreignKeys = @ForeignKey(entity = User.class,
//                           parentColumns = "uid",
//                            childColumns = "userKey",
//                                onDelete = CASCADE),
//        indices = {@Index(value = "id_msg", unique = true)})

@Entity(indices = {@Index(value = "id_msg", unique = true)})
public class Message {
    @PrimaryKey(autoGenerate = true)
    int uid;

    @ColumnInfo(name = "id_msg")
    private String id;

    @ColumnInfo(name = "username")
    private String author;

    @ColumnInfo(name = "userKey")
    private String authorKey;

    @ColumnInfo(name = "msg")
    private String message;

    @ColumnInfo(name = "date")
    private String dateCreated;

    @ColumnInfo(name = "read")
    private boolean alreadyReturned;

    @ColumnInfo(name = "sender")
    private boolean currentUser;

    @ColumnInfo(name = "send")
    private boolean send;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isAlreadyReturned() {
        return alreadyReturned;
    }

    public void setAlreadyReturned(boolean alreadyReturned) {
        this.alreadyReturned = alreadyReturned;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getAuthorKey() {
        return authorKey;
    }

    public void setAuthorKey(String authorKey) {
        this.authorKey = authorKey;
    }
}
