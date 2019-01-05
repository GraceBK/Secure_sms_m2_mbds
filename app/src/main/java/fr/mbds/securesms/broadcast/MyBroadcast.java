package fr.mbds.securesms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fr.mbds.securesms.service.MyServiceFetchMessage;

public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Log.i(MyBroadcast.class.getSimpleName(), "Service Start");

            Intent intent1 = new Intent(context, MyServiceFetchMessage.class);
            context.startService(intent1);
        } else {
            Log.i(MyBroadcast.class.getSimpleName(), "Service Start");

            Intent intent1 = new Intent(context, MyServiceFetchMessage.class);
            context.startService(intent1);
        }
    }
}
