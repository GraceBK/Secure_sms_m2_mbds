package fr.mbds.securesms.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.mbds.securesms.R;
import fr.mbds.securesms.utils.MyURL;

public class ChatFragment extends Fragment {

    private TextView res;
    private TextView resAuthor;
    private TextView resSms;
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
        resAuthor = rootView.findViewById(R.id.show_author);
        resAuthor.setText("");
        resSms = rootView.findViewById(R.id.show_sms);
        resSms.setText("");
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

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, MyURL.GET_SMS.toString(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getContext(), "GOOD "+response, Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject sms = response.getJSONObject(i);
                                String author = sms.getString("author");
                                String msg = sms.getString("msg");
                                String dateCreated = sms.getString("dateCreated");

                                resAuthor.append(author);
                                resAuthor.append("\n\n");
                                resSms.append("Author : "+author+"\n"+"message : "+msg);
                                resSms.append("\n\n");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR SMS", error.toString());
                    }
                })

        /*JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, MyURL.GET_SMS.toString(), null,
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
                })*/
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString("access_token", "No Access token");
                Log.e("--->", auth);

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(arrayRequest);
    }

    public void changeDataPropriete(Bundle bundle) {
        res.setText(bundle.getString("USERNAME"));
    }

}
