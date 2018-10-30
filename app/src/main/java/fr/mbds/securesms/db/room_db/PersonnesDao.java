package fr.mbds.securesms.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PersonnesDao {

    @Query("SELECT * FROM personnes")
    List<Personnes> getAllPersonnes();

    @Query("SELECT * FROM personnes")
    LiveData<List<Personnes>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPersonnes(Personnes... personnes);

}
