package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
        addNewUser(username);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                requestCreateMsg(username, "PING[|]" + db.userDao().getUser(username).getPublicKey());
            }
        };
        handler.postDelayed(runnable, 1000);

/*
        byte[] publicBytes = Base64.decode(puKBytes, 0);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);

        KeyFactory keyFactory;
        PublicKey publicKey1 = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey1 = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        assert publicKey1 != null;
        Log.e("-----", "String to PublicKey");
        Log.w("-----", "[ getAlgorithm ]" + publicKey1.getAlgorithm());
        Log.w("-----", "[ getFormat ]" + publicKey1.getFormat());
        Log.w("-----", "[ getEncoded ]" + Arrays.toString(publicKey1.getEncoded()));

------------------------------------------------------------------------------------------------




        // DONE : ENCODE
        byte[] encodeTxt = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey1);
            encodeTxt = cipher.doFinal(secKBytes);
            Log.i("[ENCODE]", new String(encodeTxt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        // DONE : DECODE
        byte[] decodeTxt;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decodeTxt = cipher.doFinal(encodeTxt);
            Log.i("[DECODE]", new String(decodeTxt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }




        ------------------------------------------------------------------------------------------------*/
    }

    @SuppressLint("StaticFieldLeak")
    private void addNewUser(String username) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

/*------------------------------------------------------------------------------------------------*/
        KeyPairGenerator keyPairGenerator;
        KeyPair keyPair = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            keyPair = keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPair != null;
        // Log.w("-----", "[ Key Pair (pub) ]" + Arrays.toString(keyPair.getPublic().getEncoded()));
        // Log.e("-----", "[ Key Pair (pri) ]" + Arrays.toString(keyPair.getPrivate().getEncoded()));
/*------------------------------------------------------------------------------------------------*/
        PublicKey publicKey = keyPair.getPublic();

        assert publicKey != null;
        Log.d("-----", "Get PublicKey");
        // Log.w("-----", "[ getAlgorithm ]" + publicKey.getAlgorithm());
        // Log.w("-----", "[ getFormat ]" + publicKey.getFormat());
        // Log.w("-----", "[ getEncoded ]" + Arrays.toString(publicKey.getEncoded()));
        byte[] puKBytes = Base64.encode(publicKey.getEncoded(), 0);
        String publicK = new String(puKBytes);
        // Log.i("-----", "-----\n-----BEGIN PUBLIC KEY-----\n" + publicK + "-----END PUBLIC KEY-----\n");

        PrivateKey privateKey = keyPair.getPrivate();

        assert privateKey != null;
        // Log.d("-----", "Get PrivateKey");
        // Log.w("-----", "[ getAlgorithm ]" + privateKey.getAlgorithm());
        // Log.w("-----", "[ getFormat ]" + privateKey.getFormat());
        // Log.w("-----", "[ getEncoded ]" + Arrays.toString(privateKey.getEncoded()));
        byte[] priBytes = Base64.encode(privateKey.getEncoded(), 0);
        String privateK = new String(priBytes);
        // Log.i("-----", "-----\n-----BEGIN PRIVATE KEY-----\n" + privateK + "-----END PRIVATE KEY-----\n");


        User user = new User();
        user.setUsername(username.replaceAll("\\s+$", ""));
        user.setThumbnail(r + "-" + g + "-" + b);
        user.setStatus("WAIT_PONG");

        user.setPublicKey(publicK);
        user.setPrivateKey(privateK);

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
                        // TODO : Update status to WAIT_PONG
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
