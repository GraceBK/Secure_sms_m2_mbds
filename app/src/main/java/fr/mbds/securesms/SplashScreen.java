package fr.mbds.securesms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import fr.mbds.securesms.service.MyServiceFetchMessage;
import fr.mbds.securesms.utils.MyURL;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_splash_screen);

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();

        // Log.e("[ACCESS TOKEN]", "-------------"+preferences.getString(getString(R.string.access_token), "No Access token"));
        // Log.e("[EXPIRES IN]", "-------------"+preferences.getInt(getString(R.string.expires_in), 0));



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

}
