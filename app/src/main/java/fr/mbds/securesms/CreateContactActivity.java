package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.User;

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
                    createData(username.getText().toString());
                    // TODO : Send clef public
                    onBackPressed();
                } else {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void createData(String username) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        User user = new User();
        user.setUsername(username.replaceAll("\\s+$", ""));
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

    public void initView() {
        username = findViewById(R.id.create_username);
    }
}
