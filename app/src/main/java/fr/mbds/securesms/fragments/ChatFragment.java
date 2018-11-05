package fr.mbds.securesms.fragments;

import android.content.Context;
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

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Cree un nouvelle Instance
     * @param TEXT
     * @return
     */
    /*public static ChatFragment newInstance(String TEXT) {
        ChatFragment f = new ChatFragment();

        Bundle args = new Bundle();
        args.putString("TEXT", TEXT);
        f.setArguments(args);

        return f;
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        res = rootView.findViewById(R.id.show);
        //Bundle args = new Bundle();
        //args.getString("TEXT");

        //args = getArguments();
        //if (args != null) {
        Log.e("@@@@@@", ""+getArguments());
        if (getArguments() != null) {
            Log.e("RECEIVE", getArguments()+"");
            res.setText(getArguments().getString("USERNAME"));
        }
        //} else {
          //  res.setText("vide");
        //}

        return rootView;
    }

    //@Override
    //public void onAttach(Context context) {
      //  super.onAttach(context);
        /*if (context instanceof ListContactFragment.iCallable) {
            listener = (ListContactFragment.iCallable) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement iCallable");
        }*/
    //}

    public void setBundle(Bundle bundle) {
        Log.i("ChatFragment2", bundle.toString() + " " + bundle.getString("USERNAME"));
        res.setText(bundle.getString("USERNAME"));
        Log.i("ChatFragmenuiuyggèiuhi1", bundle.toString());
    }

}
