package fr.mbds.securesms.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message message);

    @Update
    void update(Message... messages);

    @Delete
    void delete(Message... messages);

    @Query("SELECT * FROM message")
    List<Message> getAllMessages();

    @Query("SELECT * FROM message WHERE username = :user")
    List<Message> findAllMsgByUser(String user);

    @Query("SELECT * FROM message ORDER BY date DESC")
    LiveData<List<Message>> getLiveDataAllMsg();

    @Query("SELECT * FROM message WHERE username = :user ORDER BY date(date)")
    LiveData<List<Message>> loadMessageForMsgUser(String user);

}
