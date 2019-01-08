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

    @Query("SELECT * FROM User WHERE username = :user")
    LiveData<User> geOnetUser(String user);

    @Query("UPDATE User SET status = :newStatus WHERE username = :user")
    void updateUserStatus(String user, String newStatus);

    @Query("UPDATE User SET aesKey = :aesKey WHERE username = :user")
    void updateUserAes(String user, String aesKey);

    @Query("UPDATE User SET status = :newStatus, publicKey = :pubKey WHERE username = :user")
    void updateUser2(String user, String newStatus, String pubKey);

    @Query("UPDATE User SET status = :newStatus, publicKey = :pubKey WHERE username = :user")
    void updateUserPublicKey(String user, String newStatus, String pubKey);

    @Query("UPDATE User SET status = :newStatus, publicKey = :pubKey WHERE username = :user")
    void updateUserPrivateKey(String user, String newStatus, String pubKey);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User... users);

}
