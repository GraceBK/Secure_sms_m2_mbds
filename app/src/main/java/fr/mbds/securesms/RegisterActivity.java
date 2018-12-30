package fr.mbds.securesms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import fr.mbds.securesms.utils.MyURL;

public class RegisterActivity extends AppCompatActivity {

    EditText et_confirm_password;
    EditText et_username;
    EditText et_password;
    Button btn_create;
    TextView tv_go_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComposant();
        actionComposant();
    }

    public void register() {
        if (!et_username.getText().toString().equals("") && !et_password.getText().toString().equals("")
                && !et_confirm_password.getText().toString().equals("")) {
            if (et_password.getText().toString().equals(et_confirm_password.getText().toString())) {
                //btn_create.setBackgroundColor(Color.GREEN);
                requestCreateUser(et_username.getText().toString(), et_password.getText().toString());
            } else {
                Toast.makeText(this, "Password different", Toast.LENGTH_SHORT).show();
            }
        } else {
            //btn_create.setBackgroundColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Champs vide", Toast.LENGTH_LONG).show();
        }
    }

    public void requestCreateUser(final String username, final String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            Log.e("[ERROR JSONObject]", jsonObject.toString());
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, MyURL.CREATE_USER.toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("[REGISTER]", response.toString());
                        // TODO : Generer Private and Public key
                        // TODO : Save current state (is connect) in SharePreference
                        Intent goToMain = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Contact existant", Toast.LENGTH_SHORT).show();
                        clearAllEditText();
                        Log.e("[ERROR REGISTER]", error.toString());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(objectRequest);
    }

    public void actionComposant() {
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        tv_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                //goToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(goToLogin);
                //finish();
            }
        });
    }

    public void clearAllEditText() {
        et_username.getText().clear();
        et_password.getText().clear();
        et_confirm_password.getText().clear();
    }

    public void initComposant() {
        et_username = findViewById(R.id.register_et_username);
        et_password = findViewById(R.id.register_et_password);
        et_confirm_password = findViewById(R.id.register_et_password2);
        tv_go_login = findViewById(R.id.register_tv_go_login);
        btn_create = findViewById(R.id.register_btn_create);
    }
}
