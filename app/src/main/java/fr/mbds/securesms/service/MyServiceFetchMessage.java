package fr.mbds.securesms.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.mbds.securesms.MainActivity;
import fr.mbds.securesms.R;
import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Message;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.utils.MyURL;
import fr.mbds.securesms.view_model.MyViewModelFactory;

public class MyServiceFetchMessage extends Service {

    // Attribut de typr IBinder
    private final IBinder iBinder = new MonBinder();
    // Le service en lui-meme
    private MyServiceFetchMessage serviceFetchMessage;
    private NotificationManager notificationManager;

    private AppDatabase db;

    private static final long DEFAULT_SYNC_INTERVAL = 5 * 1000;    // 3 min (180000 ms)

    private Handler handler;

    private Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            // Code a executer de facon periodique

            fetchSMS();

            handler.postDelayed(this, 5000);
        }
    };

    private void newNotification(String name) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notify_channel_id");

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message) + " de " + name)
                //.setTicker(getText(R.string.ticker_text))
                .setPriority(Notification.PRIORITY_MAX)
                    /*//Vibration
                    .setVibrate(new long[] { 1000, 1000 })
                    //LED
                    .setLights(Color.RED, 3000, 3000)
                    //Sound
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)*/;

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "0001";
            NotificationChannel channel = new NotificationChannel(channelId, "Test", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        notificationManager.notify(m, builder.build());

    }


    // Interface de connexion au service
    private ServiceConnection connection = new ServiceConnection() {
        // Se declenche quand l'activite se connecte au service
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceFetchMessage = ((MyServiceFetchMessage.MonBinder) iBinder).getService();
            Toast.makeText(getApplicationContext(), "Service connected", Toast.LENGTH_SHORT).show();
        }
        // Se declanche des que le service est deconnecte
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceFetchMessage = null;
            Toast.makeText(getApplicationContext(), "Service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service starting", Toast.LENGTH_SHORT).show();
        handler = new Handler();
        handler.post(runnable); // On redemande toute les 3 min
        // Si le service est tué, recommencez
        return START_STICKY;
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(runnable, DEFAULT_SYNC_INTERVAL); // On redemande toute les 3 min
        //bindService(new Intent(this, MyServiceFetchMessage.class), connection, BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(), "Service Create", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        /*fetchSMS();
        return iBinder;*/
        throw new UnsupportedOperationException("Not yet implemented");
    }









    public void fetchSMS() {
        Toast.makeText(getApplicationContext(), "FETCH", Toast.LENGTH_SHORT).show();
        Log.d("[FETCH]", "-----> FETCH");
        db = AppDatabase.getDatabase(getApplicationContext());
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, MyURL.GET_SMS.toString(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getContext(), "GOOD "+response, Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject sms = response.getJSONObject(i);
                                String idMsg = sms.getString("id");
                                String author = sms.getString("author");
                                String body = sms.getString("msg");
                                String dateCreated = sms.getString("dateCreated");
                                boolean alreadyReturned = sms.getBoolean("alreadyReturned");
                                boolean currentUser = false;

                                saveNewContact(author);

                                if (!alreadyReturned) {
                                    newNotification(author);
                                    saveNewMsg(idMsg, author, body, dateCreated, alreadyReturned, currentUser);
                                } else {
                                    // Todo : gerer la frequence des fetch
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
                        Log.e("[ERROR SMS]", error.toString());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString(getString(R.string.access_token), "No Access token");

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(arrayRequest);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveNewContact(String username) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        User user = new User();
        user.setUsername(username);
        user.setThumbnail(r + "-" + g + "-" + b);

        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... personnes) {
                for (User personne : personnes) {
                    db.userDao().insertUser(personne);
                }
                return null;
            }
        }.execute(user);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveNewMsg(final String id, final String username, final String body, String date, boolean alreadyReturned, boolean currentUser) {

        if (body.length() > 6) {
            if (body.substring(0, 7).equals("PING[|]")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.userDao().updateUser(username, body.substring(7));
                        return null;
                    }
                }.execute();
            }
            if (body.substring(0, 7).equals("PONG[|]")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.userDao().updateUser(username, body.substring(7));
                        return null;
                    }
                }.execute();
            }
        }

        Message message = new Message();
        message.setId(id);
        message.setAuthor(username);
        message.setMessage(body);
        message.setDateCreated(date);
        message.setAlreadyReturned(alreadyReturned);
        message.setCurrentUser(currentUser);

        new AsyncTask<Message, Void, Void>() {
            @Override
            protected Void doInBackground(Message... messages) {
                for (Message message1 : messages) {
/*                    if (message1.getMessage().substring(0, 6).equals("PING[|]") && message1.getMessage().length() > 6) {
                        // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                        db.userDao().getUser(username).setIdPubKey(message1.getMessage().substring(7));
                        break;
                    } else if (message1.getMessage().substring(0, 6).equals("PONG[|]") && message1.getMessage().length() > 6) {
                        // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                        db.userDao().getUser(username).setIdPubKey(message1.getMessage().substring(7));
                    } else {*/
                    if (!message1.getMessage().contains("[|]")) {
                        db.messageDao().insert(message1);
                    }
                }
                return null;
            }
        }.execute(message);
    }


















    /**
     * Le Binder est représenté par une classe interne
     */
    public class MonBinder extends Binder {
        // Le Binder possède une méthode pour renvoyer le Service
        public MyServiceFetchMessage getService() {
            return MyServiceFetchMessage.this;
        }
    }
}
