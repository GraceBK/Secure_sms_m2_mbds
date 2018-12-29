package fr.mbds.securesms.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Message;

public class MessageViewModel extends AndroidViewModel {

    private LiveData<List<Message>> messageList;

    public MessageViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        messageList = db.messageDao().getLiveDataAllMsg();
    }

    public LiveData<List<Message>> getMessageList() {
        return messageList;
    }
}
