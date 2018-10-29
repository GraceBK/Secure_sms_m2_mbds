package fr.mbds.securesms;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import fr.mbds.securesms.fragments.ChatFragment;
import fr.mbds.securesms.fragments.ListContactFragment;

public class MainActivity extends AppCompatActivity {

    FrameLayout fl_list;
    FrameLayout fl_chat;
    FloatingActionButton floatingActionButton;

    ChatFragment chatFragment = new ChatFragment();
    ListContactFragment listContactFragment = new ListContactFragment();

    boolean a = true;
    boolean isLand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_list = findViewById(R.id.main_fl_list);
        fl_chat = findViewById(R.id.main_fl_chat);

        int orientation = getResources().getConfiguration().orientation;

        updateDisplay(orientation);
/*
        frameLayout = findViewById(R.id.main_frame_layout);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = !a;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (a) {
                    fragmentTransaction.replace(frameLayout.getId(), listContactFragment);
                } else {
                    fragmentTransaction.replace(frameLayout.getId(), chatFragment);
                }

                fragmentTransaction.commit();
            }
        });
*/
    }

    private void updateDisplay(int orientation)
    {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Toast.makeText(this, "Landscape", Toast.LENGTH_SHORT).show();
            fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            fragmentTransaction.replace(fl_chat.getId(), chatFragment);
            fragmentTransaction.commit();
        }
        else
        {
            Toast.makeText(this, "Portrait", Toast.LENGTH_SHORT).show();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(fl_list.getId(), listContactFragment);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        updateDisplay(newConfig.orientation);
    }
}
