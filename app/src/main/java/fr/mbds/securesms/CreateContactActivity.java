package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Personnes;
import fr.mbds.securesms.fragments.ChatFragment;

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

        FloatingActionButton fab = findViewById(R.id.fab_create);
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
        Personnes personnes = new Personnes();
        personnes.setUsername(username);

        new AsyncTask<Personnes, Void, Void>() {
            @Override
            protected Void doInBackground(Personnes... personnes) {
                for (Personnes personne : personnes) {
                    db.personnesDao().insertPersonnes(personne);
                }
                return null;
            }
        }.execute(personnes);
    }

    public void initView() {
        username = findViewById(R.id.create_username);
    }
}
