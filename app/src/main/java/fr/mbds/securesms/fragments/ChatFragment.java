package fr.mbds.securesms.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.mbds.securesms.R;
import fr.mbds.securesms.adapters.MyMsgAdapter;
import fr.mbds.securesms.db.room_db.AppDatabase;
import fr.mbds.securesms.db.room_db.Message;
import fr.mbds.securesms.db.room_db.User;
import fr.mbds.securesms.utils.MyURL;
import fr.mbds.securesms.view_model.MessageViewModel;
import fr.mbds.securesms.view_model.MyViewModelFactory;

public class ChatFragment extends Fragment {

    private TextView res;
    private EditText editSms;
    private Button send;
    //private Button btnSendPing;
    private Button btnSendPong;

    private ListView listView;
    private MyMsgAdapter adapter;

    private AppDatabase db;
    MessageViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        editSms = rootView.findViewById(R.id.edt_msg);
        send = rootView.findViewById(R.id.btn_send_msg);
        //btnSendPing = rootView.findViewById(R.id.btn_ping);
        btnSendPong = rootView.findViewById(R.id.btn_pong);

        listView = rootView.findViewById(R.id.conversation);

        adapter = new MyMsgAdapter(getActivity().getApplicationContext());
        listView.setAdapter(adapter);

        res = rootView.findViewById(R.id.show);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        btnSendPong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPong(res.getText().toString());
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Bundle args = getArguments();
        db = AppDatabase.getDatabase(getActivity().getApplicationContext());

        try {

            assert getArguments() != null;
            editSms.setText(getArguments().getString("TXT_SHARED"));

            User tt = db.userDao().getUser(Objects.requireNonNull(args).getString("USERNAME"));

            String t = db.userDao().getUser(Objects.requireNonNull(args).getString("USERNAME")).getIdPubKey();

            Toast.makeText(getContext(), tt.getUsername()+" pub key "+tt.getIdPubKey(), Toast.LENGTH_LONG).show();
            Log.w("[GRACE]", Objects.requireNonNull(args).getString("USERNAME")+" setIdPubKey = " + t);

            if (tt.getIdPubKey().equals("SEND_PING")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getIdPubKey().equals("WAIT_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getIdPubKey().equals("SEND_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.VISIBLE);

                viewModel = ViewModelProviders.of(this, new MyViewModelFactory(this.getActivity().getApplication(), Objects.requireNonNull(args).getString("USERNAME"))).get(MessageViewModel.class);
                viewModel.getMessageList().observe(this, new Observer<List<Message>>() {
                    @Override
                    public void onChanged(@Nullable List<Message> messages) {
                        adapter.addManyMassage(messages);
                        //Toast.makeText(getContext(), "Message " + messages.get(adapter.getCount() - 1).getId(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("[ERROR onStart]", ""+e);
        }

        if (args != null) {
            res.setText(args.getString("USERNAME"));
            // requestGetSMS();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void sendPong(final String username) {

        KeyStore keyStore;
        PrivateKey privateKey = null;

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            KeyStore.Entry entry = keyStore.getEntry("grace", null);

            privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        requestCreateMsg(username, "PONG[|]" + "clefAESchiffreeAvecKpA");

        db.userDao().updateUser(username, "SECURE");
    }


    public void sendMsg() {
        if (!Objects.equals(editSms.getText().toString(), "")) {
            if (!Objects.equals(res.getText().toString(), "")) {
                saveLocalNewMsg(res.getText().toString(), editSms.getText().toString(), false, true);

                // TODO : send server
                requestCreateMsg(res.getText().toString(), editSms.getText().toString());
            } else {
                Toast.makeText(getContext(), "Message LLLLLL", Toast.LENGTH_LONG).show();
            }
            editSms.setText("");
        } else {
            Toast.makeText(getContext(), "Message vide " +res.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void saveLocalNewMsg(String username, String body, boolean alreadyReturned, boolean currentUser) {

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String currentDate = dateFormat.format(new Date());


        Message message = new Message();
        message.setAuthor(username);
        message.setMessage(body);
        message.setDateCreated(currentDate);
        message.setAlreadyReturned(alreadyReturned);
        message.setCurrentUser(currentUser);

        new AsyncTask<Message, Void, Void>() {
            @Override
            protected Void doInBackground(Message... messages) {
                for (Message message1 : messages) {
                    db.messageDao().insert(message1);
                }
                return null;
            }
        }.execute(message);
    }

    /**
     * Function to send SMS
     * @param receiver
     * @param message
     */
    public void requestCreateMsg(final String receiver, final String message) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("receiver", receiver);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            Log.e("ERROR JSONObject", jsonObject.toString());
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, MyURL.SEND_SMS.toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("SEND OK", response.toString());
                        // TODO : save Local
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Message pas envoye", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR SEND", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                String auth = sharedPref.getString("access_token", "No Access token");

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+auth);
                return headers;
            }
        };
        queue.add(objectRequest);
    }

    public void changeDataPropriete(final Bundle bundle) {
        res.setText(bundle.getString("USERNAME"));
        /*if (!Objects.equals(bundle.getString("MESSAGES"), "")) {
            Log.e("MESSAGES", bundle.getString("USERNAME")+"*****"+bundle.getString("MESSAGES"));
            try {
                //Log.w("MESSAGES", "*****"+db.messageDao().loadMessageForMsgUser(bundle.getString("USERNAME")));
                viewModel = ViewModelProviders.of(this, new MyViewModelFactory(this.getActivity().getApplication(), bundle.getString("USERNAME"))).get(MessageViewModel.class);
                viewModel.getMessageList().observe(this, new Observer<List<Message>>() {
                    @Override
                    public void onChanged(@Nullable List<Message> messages) {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        adapter.addManyMassage(messages);
                        for (Message a : messages) {
                            Log.w("MESSAGES", "*****"+a.getMessage());
                        }
                        adapter.addManyMassage(messages);

                        //Toast.makeText(getContext(), "Message " + messages.get(adapter.getCount() - 1).getId(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e("", ""+e);
            }
            adapter.notifyDataSetInvalidated();
        }*/

        try {

            assert getArguments() != null;
            editSms.setText(getArguments().getString("TXT_SHARED"));

            User tt = db.userDao().getUser(bundle.getString("USERNAME"));

            String t = db.userDao().getUser(bundle.getString("USERNAME")).getIdPubKey();

            Toast.makeText(getContext(), tt.getUsername()+" pub key "+tt.getIdPubKey(), Toast.LENGTH_LONG).show();
            Log.w("[GRACE]", bundle.getString("USERNAME")+" setIdPubKey = " + t);

            if (tt.getIdPubKey().equals("SEND_PING")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getIdPubKey().equals("WAIT_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getIdPubKey().equals("SEND_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.VISIBLE);

                viewModel = ViewModelProviders.of(this, new MyViewModelFactory(this.getActivity().getApplication(), bundle.getString("USERNAME"))).get(MessageViewModel.class);
                viewModel.getMessageList().observe(this, new Observer<List<Message>>() {
                    @Override
                    public void onChanged(@Nullable List<Message> messages) {
                        adapter.addManyMassage(messages);
                        //Toast.makeText(getContext(), "Message " + messages.get(adapter.getCount() - 1).getId(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("[ERROR onStart]", ""+e);
        }
    }





















    /*
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
                                Log.i("SMS", "@@@@@@@@@@@@@@@@@@@@@@@"+sms);

                                //adapter.add(new Message(author, msg, true));
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
    */

}
