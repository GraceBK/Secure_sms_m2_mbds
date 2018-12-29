package fr.mbds.securesms.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM message")
    List<Message> getAllMessages();

    @Query("SELECT * FROM message WHERE username = :user")
    List<Message> getAllMsgByUser(String user);

    @Query("SELECT * FROM message")
    LiveData<List<Message>> getLiveDataAllMsg();

}
