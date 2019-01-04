package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.mbds.securesms.proxy_for_emulator.ProxyHurlStack;
import fr.mbds.securesms.service.MyServiceFetchMessage;
import fr.mbds.securesms.utils.MyURL;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    Button btn_login;
    TextView tv_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComposant();
        actionComposant();

    }

    public void login() {
        if (!et_username.getText().toString().equals("") && !et_password.getText().toString().equals("")) {
            //btn_login.setBackgroundColor(Color.GREEN);
            requestLogin(et_username.getText().toString(), et_password.getText().toString());
        } else {
            //btn_login.setBackgroundColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Champs vide", Toast.LENGTH_LONG).show();
        }
    }


    public void requestLogin(final String username, final String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            Log.e("[LOGIN JSONObject]", jsonObject.toString());
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, MyURL.LOGIN.toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String access_token;
                        int expires_in;
                        //Log.i("LOGIN", response.toString());
                        Log.i("[LOGIN]", "ok");
                        try {
                            access_token = response.getString("access_token");
                            expires_in = response.getInt("expires_in");

                            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.access_token), access_token);
                            editor.putInt(getString(R.string.expires_in), expires_in);
                            editor.apply();
                            Log.i("[LOGIN]", "access token saving");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Intent service = new Intent(LoginActivity.this, MyServiceFetchMessage.class);
                        startService(service);


                        Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        //clearAllEditText();
                        Log.e("[LOGIN]", error.toString());
                    }
                });
        queue.add(objectRequest);
    }

    public void actionComposant() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                //goToRegister.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(goToRegister);
            }
        });
    }

    public void initComposant() {
        et_username = findViewById(R.id.login_et_username);
        et_password = findViewById(R.id.login_et_password);
        tv_create = findViewById(R.id.login_tv_create_account);
        btn_login = findViewById(R.id.login_btn_login);
        et_username.setText("azerty");
        et_password.setText("azerty");
    }
}
