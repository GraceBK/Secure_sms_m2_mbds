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
public interface UserDao {

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

    @Query("SELECT * FROM User")
    List<User> getAllUser();

    @Query("SELECT * FROM User WHERE username = :user")
    User getUser(String user);

    @Query("UPDATE User SET indexToPubKey = :newidPubKey WHERE username = :user")
    void updateUser(String user, String newidPubKey);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User... users);

    @Query("DELETE FROM User")
    void delete();

}
