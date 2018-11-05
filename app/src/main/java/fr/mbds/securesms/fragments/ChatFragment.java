package fr.mbds.securesms.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.mbds.securesms.R;

public class ChatFragment extends Fragment {

    private TextView res;
/*
    public ChatFragment() {
        // Required empty public constructor
    }
*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        res = rootView.findViewById(R.id.show);
        return rootView;
    }

    public void changeDataPropriete(Bundle bundle) {
        Log.i("ChatFragment2", bundle.toString() + " " + bundle.getString("USERNAME"));
        res.setText(bundle.getString("USERNAME"));
        Log.i("ChatFragmenuiuyggèiuhi1", bundle.toString());
    }

}
