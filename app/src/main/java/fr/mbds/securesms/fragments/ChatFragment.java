package fr.mbds.securesms.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            res.setText(args.getString("USERNAME"));
        }
    }

    public void changeDataPropriete(Bundle bundle) {
        res.setText(bundle.getString("USERNAME"));
    }

}
