package fr.mbds.securesms.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import fr.mbds.securesms.R;
import fr.mbds.securesms.adapters.MyUserAdapter;
import fr.mbds.securesms.models.User;

public class ListContactFragment extends Fragment {

    private EditText test;
    private Button send;
    private FloatingActionButton fab;


    private RecyclerView recyclerView;
    private MyUserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> userArrayList = new ArrayList<>();


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

        recyclerView = rootView.findViewById(R.id.list_contact_rc);

        adapter = new MyUserAdapter(userArrayList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                if(motionEvent.getAction() != MotionEvent.ACTION_UP)
                    return false;

                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                int pos = recyclerView.getChildAdapterPosition(child);

                args.putString("USERNAME", userArrayList.get(pos).getUsername());
                args.putString("RESUME", userArrayList.get(pos).getResume());
                fragment.setArguments(args);
                Log.e("SEND", userArrayList.get(pos).toString()+motionEvent.getAction());

                int currentOrientation = getResources().getConfiguration().orientation;

                if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_list, fragment).addToBackStack(null);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_viewer, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });


        userArrayList.add(new User("Grace", "bcezlbfczivbzb"));
        userArrayList.add(new User("BOUKOU", "efkj"));
        userArrayList.add(new User("Coucou", "fhzjfhbzheivf"));
        userArrayList.add(new User("Grace", "bcezlbfczivbzb"));


        /*test = rootView.findViewById(R.id.text);
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
        });*/



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
