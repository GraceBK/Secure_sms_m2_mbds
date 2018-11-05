package fr.mbds.securesms;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fr.mbds.securesms.fragments.ChatFragment;
import fr.mbds.securesms.fragments.ListContactFragment;

public class MainActivity extends FragmentActivity implements iCallable {

    FrameLayout fl_list;
    FrameLayout fl_chat;
//    FloatingActionButton floatingActionButton;

    ChatFragment chatFragment = new ChatFragment();
    ListContactFragment listContactFragment = new ListContactFragment();

    boolean a = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        updateDisplay();


        /*floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = !a;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (a) {
                    fragmentTransaction.replace(fl_list.getId(), listContactFragment);
                } else {
                    fragmentTransaction.replace(fl_chat.getId(), chatFragment);
                }

                fragmentTransaction.commit();
            }
        });*/

    }

    private void updateDisplay() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setupFullScreenMode();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            fragmentTransaction.replace(fl_chat.getId(), chatFragment);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (!listContactFragment.isSwipe()) {
                fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", "Coucou");
                Log.e("aaaaaaa", "------");
                //chatFragment.setBundle(bundle);
                chatFragment.setArguments(bundle);
                fragmentTransaction.replace(fl_list.getId(), chatFragment);
            }
            fragmentTransaction.commit();
            hideSystemUI();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        updateDisplay();
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
/*
        Log.d("MainActivity", bundle.toString());
        byte[] plaintext = bundle.getString("USERNAME").getBytes();
        KeyPairGenerator keygen = null;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keygen.initialize(2048);
        KeyPair keyPair = keygen.genKeyPair();

        Log.e("PUBLIC", ""+keyPair.getPublic());
        Log.e("PRIVATE", ""+keyPair.getPrivate());

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(plaintext);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        Log.d("ENCRYPT", ""+new String(cipherText));

        Cipher cipher1 = null;
        try {
            cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher1.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decryptedText = new byte[0];
        try {
            decryptedText = cipher1.doFinal(cipherText);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String decrypted = new String(decryptedText);

        Log.d("DECRYPT", ""+decrypted);
*/

        chatFragment.setBundle(bundle);

        updateDisplay();
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
