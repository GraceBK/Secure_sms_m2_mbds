package fr.mbds.securesms;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    EditText et_name;
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
        if (et_username.getText().toString().equals("") && (et_password.getText().toString().equals(""))) {
            btn_create.setBackgroundColor(Color.GREEN);
            Intent goToMain = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(goToMain);
            finish();
        } else {
            btn_create.setBackgroundColor(Color.RED);
        }
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

    public void initComposant() {
        et_name = findViewById(R.id.register_et_name);
        et_username = findViewById(R.id.register_et_username);
        et_password = findViewById(R.id.register_et_password);
        tv_go_login = findViewById(R.id.register_tv_go_login);
        btn_create = findViewById(R.id.register_btn_create);
    }
}
