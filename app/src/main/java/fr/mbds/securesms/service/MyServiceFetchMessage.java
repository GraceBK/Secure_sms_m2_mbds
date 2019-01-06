package fr.mbds.securesms.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import fr.mbds.securesms.MainActivity;
import fr.mbds.securesms.R;
import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Message;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.utils.MyURL;

public class MyServiceFetchMessage extends Service {

    // Attribut de typr IBinder
    //private final IBinder iBinder = new MonBinder();
    // Le service en lui-meme
    private NotificationManager notificationManager;

    private AppDatabase db;

    private static final long DEFAULT_SYNC_INTERVAL = 5 * 1000;    // 3 min (180000 ms)

    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Code a executer de facon periodique
            Toast.makeText(getApplicationContext(), "Le service est toujours en cours d'exécution", Toast.LENGTH_SHORT).show();
            fetchSMS();

            handler.postDelayed(runnable, 5000);
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
        //handler = new Handler();
        //handler.postDelayed(runnable, DEFAULT_SYNC_INTERVAL); // On redemande toute les 3 min
        // Si le service est tué, recommencez
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created!", Toast.LENGTH_SHORT).show();

        handler = new Handler();
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("[EXIT]", "Service done");
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
        /*Intent broadcastIntent = new Intent(this, MyServiceFetchMessage.class);
        sendBroadcast(broadcastIntent);*/
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }









    public void fetchSMS() {
        Log.i("[FETCH]", "-----> FETCH");
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
        //user.setAesKey();

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

    public static byte[] decryptAES(String seed, byte[] data) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes("UTF8"));
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(data);
    }

    private static int BLOCKS = 128;

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(BLOCKS, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    KeyStore keyStore;
    KeyStore.Entry entry;

    @SuppressLint("StaticFieldLeak")
    private void saveNewMsg(final String id, final String username, final String body, String date, boolean alreadyReturned, boolean currentUser) {

        String delimiter = "\\[\\|\\]\\s*";

        String[] tokensValues = body.split(delimiter);

        final String s1;
        final String s2;
        final String[] decrypt = new String[1];
        final PublicKey[] publicKey = new PublicKey[1];
        // final String s3;


        if (tokensValues.length >= 1) {
            s1 = tokensValues[0];
            s2 = tokensValues[1];

            if (s1.equals("PING")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        db.userDao().updateUser2(username, "SEND_PONG", s2);
                        return null;
                    }
                }.execute();
            }
            if (s1.equals("PONG")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            keyStore = KeyStore.getInstance("AndroidKeyStore");
                            keyStore.load(null);
                            publicKey[0] = keyStore.getCertificate("alice").getPublicKey();
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        try {
                            decrypt[0] = String.valueOf(decryptAES(publicKey[0].getEncoded().toString(),s2.getBytes()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        db.userDao().updateAES(username, s2);
                        db.userDao().updateUser2(username, "SECURE", s2);
                        return null;
                    }
                }.execute();
            }
            if (s1.equals("MSG")) {
                Message message = new Message();
                message.setId(id);
                message.setAuthor(username);
                message.setMessage(s2);
                message.setDateCreated(date);
                message.setAlreadyReturned(alreadyReturned);
                message.setCurrentUser(currentUser);

                new AsyncTask<Message, Void, Void>() {
                    @Override
                    protected Void doInBackground(Message... messages) {
                        for (Message message1 : messages) {
                            db.messageDao().insert(message1);
                        }
                        return null;
                    }
                }.execute(message);
            }

        }
    }

}
