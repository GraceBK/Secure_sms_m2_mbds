package fr.mbds.securesms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import fr.mbds.securesms.service.MyServiceFetchMessage;

public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkInternet(context)) {
            Log.i(MyBroadcast.class.getSimpleName(), "Service Start");

            Intent intent1 = new Intent(context, MyServiceFetchMessage.class);
            context.startService(intent1);
        } else {
            Log.i(MyBroadcast.class.getSimpleName(), "Service Start");

            Intent intent1 = new Intent(context, MyServiceFetchMessage.class);
            context.startService(intent1);
        }
    }


    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }


    public class ServiceManager {
        Context context;

        ServiceManager(Context context) {
            this.context = context;
        }

        public boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

}
