package fr.mbds.securesms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.service.MyServiceFetchMessage;
import fr.mbds.securesms.utils.MyURL;

public class SplashScreen extends AppCompatActivity/* implements ServiceConnection */{

    private AppDatabase db;

    private MyServiceFetchMessage serviceFetchMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
//        db = AppDatabase.getDatabase(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();

        Log.e("[ACCESS TOKEN]", "-------------"+preferences.getString(getString(R.string.access_token), "No Access token"));
        Log.e("[EXPIRES IN]", "-------------"+preferences.getInt(getString(R.string.expires_in), 0));

        KeyPairGenerator keyPairGenerator;

        KeyGenParameterSpec.Builder builder;

        KeyPair keyPair;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;


        KeyGenerator keyGenerator;
        KeyGenParameterSpec.Builder builder2;
        SecretKey secretKey;


        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            builder = new KeyGenParameterSpec.Builder("alice", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
            try {
                keyPairGenerator.initialize(builder.build());
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            //keyPairGenerator.initialize(2048);


            keyPair = keyPairGenerator.genKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            builder2 = new KeyGenParameterSpec.Builder("bob", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
            secretKey = keyGenerator.generateKey();

            //new KryptosAES().dechiffrement(new KryptosAES().chiffrement("Grace", publicKey), privateKey);

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }/* catch ( InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }*/

        Log.w("[public]", "-------------" + publicKey);
        Log.w("[privee]", "-------------" + privateKey);

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

}
