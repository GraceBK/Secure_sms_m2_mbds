package fr.mbds.securesms;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        if (et_username.getText().toString().equals("") && (et_password.getText().toString().equals(""))) {
            btn_login.setBackgroundColor(Color.GREEN);
            Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(goToMain);
            finish();
        } else {
            btn_login.setBackgroundColor(Color.RED);
        }
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
    }
}
