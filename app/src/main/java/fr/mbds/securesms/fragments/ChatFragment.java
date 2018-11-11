package fr.mbds.securesms.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

import fr.mbds.securesms.R;
import fr.mbds.securesms.utils.MyURL;

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
            requestGetSMS();
        }
    }

    public void requestGetSMS() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, MyURL.GET_SMS.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("SMS", "---->"+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
                        //clearAllEditText();
                        Log.e("ERROR SMS", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = SyncStateContract.Constants;

                return super.getHeaders();
            }
        };
        queue.add(objectRequest);
    }

    public void changeDataPropriete(Bundle bundle) {
        res.setText(bundle.getString("USERNAME"));
    }

}
