package fr.mbds.securesms.fragments;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import fr.mbds.securesms.R;

public class ListContactFragment extends Fragment {

    private EditText test;
    private Button send;
    private FloatingActionButton fab;

    private Bundle args;

    private Fragment fragment = new ChatFragment();

    public ListContactFragment() {
        // Required empty public constructor
        args = new Bundle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_contact, container, false);

        test = rootView.findViewById(R.id.text);
        send = rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                args.putString("TEXT", test.getText().toString());
                fragment.setArguments(args);
                Log.e("SEND", test.getText().toString());

                int currentOrientation = getResources().getConfiguration().orientation;

                if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_list, fragment).addToBackStack(null);
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_viewer, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });



        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_list, fragment).addToBackStack(null);
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_viewer, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
