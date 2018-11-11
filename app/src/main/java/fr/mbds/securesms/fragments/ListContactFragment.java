package fr.mbds.securesms.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Random;

import fr.mbds.securesms.CreateContactActivity;
import fr.mbds.securesms.R;
import fr.mbds.securesms.adapters.MyUserAdapter;
import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Personnes;
import fr.mbds.securesms.view_model.PersonnesViewModel;

public class ListContactFragment extends Fragment {

    InterfaceClickListener callback;

    public interface InterfaceClickListener {

        void transferData(Bundle bundle);

        void clickItem(boolean click);

    }

    private FloatingActionButton fab;


    private RecyclerView recyclerView;
    private MyUserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Personnes> personnesList;

    private AppDatabase db;
    PersonnesViewModel viewModel;

    long down = 0;
    long diff = 0;


    private Bundle args;

    private boolean swipe = false;

    //private Fragment fragment = new ChatFragment();

    public ListContactFragment() {
        args = new Bundle();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InterfaceClickListener) {
            callback = (InterfaceClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement callback");
        }
    }

    /*@Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_contact, container, false);

        recyclerView = rootView.findViewById(R.id.list_contact_rc);

        db = AppDatabase.getDatabase(getActivity().getApplicationContext());
        personnesList = db.personnesDao().getAllPersonnes();

        adapter = new MyUserAdapter(personnesList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        viewModel = ViewModelProviders.of(this).get(PersonnesViewModel.class);
        viewModel.getPersonneList().observe(this, new Observer<List<Personnes>>() {
            @Override
            public void onChanged(@Nullable List<Personnes> personnes) {
                adapter.updatePersonneList(personnes);
            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down = System.currentTimeMillis();
                        // break;
                    case MotionEvent.ACTION_UP:
                        diff = System.currentTimeMillis() - down;
                        if (diff <= 100) {
                            Log.e("ttttttttt", "-----------"+diff);

                            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                            int pos = recyclerView.getChildAdapterPosition(child);



                            try {
                                args.putString("USERNAME", personnesList.get(pos).getUsername());
                                // args.putString("RESUME", personnesList.get(pos).getResume());
                                //fragment.setArguments(args);


                                int currentOrientation = getResources().getConfiguration().orientation;
                                if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                                    // Je change le boolean
                                    setSwipe(true);
                                    click(swipe);

                                }

                                sendDataToChatFragment(args);
                            } catch (Exception e) {
                                // Log.e("ERROR", "No Element");
                            }
                        }
                        // break;
                }

                    // return false;
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createContact = new Intent(getContext(), CreateContactActivity.class);
                startActivity(createContact);
                //setData();
            }
        });

        return rootView;
    }

    public void sendDataToChatFragment(Bundle bundle) {
        if (callback != null) {
            callback.transferData(bundle);
        }
    }

    public void click(boolean click) {
        callback.clickItem(click);
    }

    @SuppressLint("StaticFieldLeak")
    private void setData() {
        Random rand = new Random();

        Personnes personnes = new Personnes();
        personnes.setUsername("BOUKOU " + rand.nextInt(50) + 1);

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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public boolean isSwipe() {
        return swipe;
    }

    public void setSwipe(boolean swipe) {
        this.swipe = swipe;
    }
}
