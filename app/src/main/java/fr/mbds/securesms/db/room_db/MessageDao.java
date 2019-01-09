package fr.mbds.securesms.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message message);

    @Query("SELECT * FROM message WHERE username = :user ORDER BY date(date)")
    LiveData<List<Message>> loadMessageForMsgUser(String user);

}
