package fr.mbds.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Personnes;
import fr.mbds.securesms.fragments.ChatFragment;
import fr.mbds.securesms.fragments.ListContactFragment;
import fr.mbds.securesms.utils.MyURL;

public class MainActivity extends FragmentActivity implements ListContactFragment.InterfaceClickListener {


    private AppDatabase db;

    FrameLayout fl_list;
    FrameLayout fl_chat;

    ChatFragment chatFragment = new ChatFragment();
    ListContactFragment listContactFragment = new ListContactFragment();

    boolean a = true;











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(getApplicationContext());

        Log.i("MainActivity", "Chargement des messages");
        // TODO : Faire de tel sorte que le chargement de nouveau message ne puisse pas bloquer l'affichage
        // l'idee serait d'utiliser des AsyncTask

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        setContentView(R.layout.activity_main);
        fl_list = findViewById(R.id.main_fl_list);
        fl_chat = findViewById(R.id.main_fl_viewer);

        if (findViewById(R.id.main_fl_list) != null) {
//            Toast.makeText(this, ""+ savedInstanceState, Toast.LENGTH_SHORT).show();
        }

        /*if (savedInstanceState == null) {
            ListContactFragment listContactFragment1 = new ListContactFragment();
            listContactFragment1.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(fl_list.getId(), chatFragment);
        }*/

        updateDisplay();

    }

    private void updateDisplay() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setupFullScreenMode();
            fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            fragmentTransaction.replace(fl_chat.getId(), chatFragment);
        } else {
            if (!listContactFragment.isSwipe()) {
                fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            } else {
                fragmentTransaction.replace(fl_list.getId(), chatFragment);
            }
//            hideSystemUI();
        }
        fragmentTransaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //updateDisplay();
        getSupportFragmentManager().beginTransaction().detach(listContactFragment).attach(listContactFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("____________________", "TESTER");
        getSupportFragmentManager().beginTransaction().detach(listContactFragment).attach(listContactFragment).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        // updateDisplay();
    }


    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // TODO : Update UI to reflect text being shared
            Log.e("SHARE TEXT", ""+sharedText);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void transferData(Bundle bundle) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            chatFragment.setArguments(bundle);
        }

        if (chatFragment != null) {
            chatFragment.changeDataPropriete(bundle);
        }
    }

    @Override
    public void clickItem(boolean click) {
        updateDisplay();
    }

    @Override
    public void onBackPressed() {
        boolean clickBack1 = listContactFragment.isSwipe();
        listContactFragment.setSwipe(false);
        boolean clickBack2 = listContactFragment.isSwipe();
        // (!A and !B) = !(A or B)
        if (!(clickBack1 || clickBack2)) {
            super.onBackPressed();
            finish();
        }
        updateDisplay();
    }
}
