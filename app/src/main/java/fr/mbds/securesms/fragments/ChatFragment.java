package fr.mbds.securesms.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

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

        Log.e("AZERTY", "-----------"+getArguments());

  //      if (getArguments() == null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.e("AZERTY", "++++"+ getArguments());
          //  return rootView;
        //} else {
          //  Log.e("AZERTY", "////"+ getArguments());
            res = rootView.findViewById(R.id.show);
            return rootView;
        //}
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
        //Log.e("AZERTY", "++++"+ getArguments());
        //Log.e("AZERTY", "++++"+ bundle.toString());
        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Objects.requireNonNull(getActivity()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
            getActivity().findViewById(R.id.chat_ll_empty).setVisibility(View.VISIBLE);
        } else {
            Objects.requireNonNull(getActivity()).findViewById(R.id.chat_ll).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.chat_ll_empty).setVisibility(View.GONE);
        }*/
        Log.i("ChatFragment2", bundle.toString() + " " + bundle.getString("USERNAME"));
        res.setText(bundle.getString("USERNAME"));
        Log.i("ChatFragmenuiuyggèiuhi1", bundle.toString());
    }

}
