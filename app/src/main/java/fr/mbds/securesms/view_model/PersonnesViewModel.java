package fr.mbds.securesms.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Personnes;

public class PersonnesViewModel extends AndroidViewModel {

    private LiveData<List<Personnes>> personneList;

    public PersonnesViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        personneList = db.personnesDao().getAll();
    }

    public LiveData<List<Personnes>> getPersonneList() {
        return personneList;
    }
}
