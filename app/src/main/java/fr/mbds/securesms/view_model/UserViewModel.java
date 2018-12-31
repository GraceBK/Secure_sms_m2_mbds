package fr.mbds.securesms.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.User;

public class UserViewModel extends AndroidViewModel {

    private LiveData<List<User>> userList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        userList = db.userDao().getAll();
    }

    public LiveData<List<User>> getUserList() {
        return userList;
    }
}
