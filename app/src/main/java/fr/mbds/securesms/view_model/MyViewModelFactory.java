package fr.mbds.securesms.view_model;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
 *
 *
 *
 *
 * https://medium.com/google-developer-experts/viewmodels-under-the-hood-f8e286c4cc72
 */
public class MyViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private String param;

    public MyViewModelFactory(Application application, String param) {
        this.application = application;
        this.param = param;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MessageViewModel(application, param);
    }
}
