package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.service.MyServiceFetchMessage;
import fr.mbds.securesms.utils.MyURL;

public class SplashScreen extends AppCompatActivity/* implements ServiceConnection */{

    private AppDatabase db;

    private MyServiceFetchMessage serviceFetchMessage;

    public void fetchSMS() {
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
                                String author = sms.getString("author");
                                String msg = sms.getString("msg");
                                String dateCreated = sms.getString("dateCreated");
                                Log.e("ERROR SMS", author + "------"+msg+"++++"+dateCreated);

                                saveNewContact(author);

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
                        Log.e("ERROR SMS", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString("access_token", "No Access token");
                Log.e("--->", auth);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
//        db = AppDatabase.getDatabase(getApplicationContext());
//        fetchSMS();
        setContentView(R.layout.activity_splash_screen);

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();

        Log.e("[ACCESS TOKEN]", "-------------"+preferences.getString(getString(R.string.access_token), "No Access token"));
        Log.e("[EXPIRES IN]", "-------------"+preferences.getInt(getString(R.string.expires_in), 0));

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                validateToken();
            }
        }, SPLASH_TIME_OUT);
    }


    public void validateToken() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, MyURL.IS_VALID_TOKEN.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("[VALIDATE]", response);

                        /*if (serviceFetchMessage != null) {
                            Toast.makeText(getApplicationContext(), "Je fais un Fetch", Toast.LENGTH_LONG).show();
                        }*/

                        Intent service = new Intent(SplashScreen.this, MyServiceFetchMessage.class);
                        startService(service);

                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("[VALIDATE]", ""+error);
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString(getString(R.string.access_token), "No Access token");

                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(request);
    }




    private void setupFullScreenMode() {
        View view = setFullScreen();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                setFullScreen();
            }
        });
    }

    private View setFullScreen() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        return view;
    }

    /*@Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MyServiceFetchMessage.MonBinder monBinder = (MyServiceFetchMessage.MonBinder) iBinder;
        serviceFetchMessage = monBinder.getService();
        Toast.makeText(this, "Service connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        serviceFetchMessage = null;
        Toast.makeText(this, "Service disconnected", Toast.LENGTH_LONG).show();
    }*/
}
