package fr.mbds.securesms.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import fr.mbds.securesms.CreateContactActivity;
import fr.mbds.securesms.R;
import fr.mbds.securesms.adapters.MyUserAdapter;
import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.view_model.UserViewModel;

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
    private List<User> userList;

    private AppDatabase db;
    UserViewModel viewModel;


    private Bundle args;

    private boolean swipe = false;

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_contact, container, false);

        recyclerView = rootView.findViewById(R.id.list_contact_rc);

        db = AppDatabase.getDatabase(getActivity().getApplicationContext());
        userList = db.userDao().getAllUser();

        adapter = new MyUserAdapter(userList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        Log.e("___________________", "COUCOU onCreateView");

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> personnes) {
                adapter.updatePersonneList(personnes);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.apply();


                    String sharedTXT = preferences.getString(getString(R.string.pref_shared_txt), "");

                    Log.e("[ACCESS TOKEN]", "-------------"+sharedTXT);

                    args.putString("USERNAME", userList.get(position).getUsername());
                    args.putString("MESSAGES", userList.get(position).getUsername());
                    args.putString("TXT_SHARED", sharedTXT);

                    final Handler handlerPONG = new Handler();
                    final Runnable runnablePONG = new Runnable() {
                        @Override
                        public void run() {
                            db.userDao().updateUserStatus(userList.get(position).getUsername(), "SECURE");
                        }
                    };
                    handlerPONG.postDelayed(runnablePONG, 1000);

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

            @Override
            public void onLongClick(View view, int position) {
                Log.e("--------->", "LONG");
                Toast.makeText(getContext(), "LONG CLICK", Toast.LENGTH_LONG).show();
                //setData();
                // TODO : Send public Key

            }
        }));

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


















    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }



    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)) {
                clickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }
}
