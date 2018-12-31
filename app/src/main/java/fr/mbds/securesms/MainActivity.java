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
import java.util.Random;

import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Message;
import fr.mbds.securesms.db.room_db.User;
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




    public void fetchSMS() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, MyURL.GET_SMS.toString(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getContext(), "GOOD "+response, Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject sms = response.getJSONObject(i);
                                String idMsg = sms.getString("id");
                                String author = sms.getString("author");
                                String msg = sms.getString("msg");
                                String dateCreated = sms.getString("dateCreated");
                                boolean alreadyReturned = sms.getBoolean("alreadyReturned");
                                boolean currentUser = false;
                                Log.i("[FETCH SMS]", sms.getString("id")+ " ===== "+ author + "------"+msg+"++++"+dateCreated);

                                saveNewContact(author);
                                saveNewMsg(idMsg, author, msg, dateCreated, alreadyReturned, currentUser);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
                        Log.e("[ERROR SMS]", error.toString());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString(getString(R.string.access_token), "No Access token");

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(arrayRequest);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveNewContact(String username) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        User user = new User();
        user.setUsername(username);
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

    @SuppressLint("StaticFieldLeak")
    private void saveNewMsg(final String id, final String username, final String body, String date, boolean alreadyReturned, boolean currentUser) {

        if (body.length() > 6) {
            //Log.w("[GRACE]", "ici "+body.substring(0, 7));
            if (body.substring(0, 7).equals("PING[|]")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                db.userDao().updateUser(username, body.substring(7));
            }
            if (body.substring(0, 7).equals("PONG[|]")) {
                // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                db.userDao().updateUser(username, body.substring(7));
            }
        }

        Message message = new Message();
        message.setId(id);
        message.setAuthor(username);
        message.setMessage(body);
        message.setDateCreated(date);
        message.setAlreadyReturned(alreadyReturned);
        message.setCurrentUser(currentUser);

        new AsyncTask<Message, Void, Void>() {
            @Override
            protected Void doInBackground(Message... messages) {
                for (Message message1 : messages) {
/*                    if (message1.getMessage().substring(0, 6).equals("PING[|]") && message1.getMessage().length() > 6) {
                        // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                        db.userDao().getUser(username).setIdPubKey(message1.getMessage().substring(7));
                        break;
                    } else if (message1.getMessage().substring(0, 6).equals("PONG[|]") && message1.getMessage().length() > 6) {
                        // TODO update user (PublicKey) /!\ ideal dans la KeyStore
                        db.userDao().getUser(username).setIdPubKey(message1.getMessage().substring(7));
                    } else {*/
                    if (!message1.getMessage().contains("[|]")) {
                        db.messageDao().insert(message1);
                    }
                }
                return null;
            }
        }.execute(message);
    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(getApplicationContext());

        fetchSMS();

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
            fragmentTransaction.detach(chatFragment).attach(chatFragment);
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
        getSupportFragmentManager().beginTransaction().detach(listContactFragment).attach(listContactFragment).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportFragmentManager().beginTransaction().remove(chatFragment).commit();
        }*/

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
