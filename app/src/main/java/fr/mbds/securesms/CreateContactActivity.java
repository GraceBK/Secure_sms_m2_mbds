package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.utils.MyURL;

public class CreateContactActivity extends AppCompatActivity {

    EditText username;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = AppDatabase.getDatabase(getApplicationContext());

        initView();

        Button fab = findViewById(R.id.fab_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!username.getText().toString().isEmpty()) {
                    // addNewUser(username.getText().toString());
                    // TODO : Send clef public
                    sendPing(username.getText().toString());
                    onBackPressed();
                } else {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void sendPing(final String username) {
        KeyStore keyStore;
        PublicKey publicKey = null;

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            publicKey = keyStore.getCertificate("alice").getPublicKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        assert publicKey != null;
        Log.w("-----", "Auteur[|]"+"getAlgorithm[|]" + publicKey.getAlgorithm());
        Log.w("-----", "Auteur[|]"+"getFormat[|]" + publicKey.getFormat());
        Log.w("-----", "Auteur[|]"+"getEncoded[|]" + Arrays.toString(publicKey.getEncoded()));


        byte[] pKBytes = Base64.encode(publicKey.getEncoded(), 0);
        String pK = new String(pKBytes);
        // String pubKey = "-----BEGIN PUBLIC KEY-----\n" + pK + "-----END PUBLIC KEY-----\n";
        Log.w("-----", "Auteur[|]" + pK);

        requestCreateMsg(username, "PING[|]" + pK);
    }

    @SuppressLint("StaticFieldLeak")
    private void addNewUser(String username) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        User user = new User();
        user.setUsername(username.replaceAll("\\s+$", ""));
        user.setThumbnail(r + "-" + g + "-" + b);
        user.setStatus("WAIT_PONG");

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



    public void requestCreateMsg(final String receiver, final String message) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("receiver", receiver);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            Log.e("ERROR JSONObject", jsonObject.toString());
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, MyURL.SEND_SMS.toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("SEND OK", response.toString());

                        addNewUser(receiver);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), receiver + " N'existe pas", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR SEND", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString("access_token", "No Access token");

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(objectRequest);
    }

    public void initView() {
        username = findViewById(R.id.create_username);
    }
}
