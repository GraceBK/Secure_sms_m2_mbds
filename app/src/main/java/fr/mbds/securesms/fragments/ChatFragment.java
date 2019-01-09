package fr.mbds.securesms.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
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

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

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
                //getFragmentManager().beginTransaction().detach(getActivity().fra).attach(this).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //startActivity(getActivity().getIntent());
                    }
                }, 1000);
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

            String t = db.userDao().getUser(Objects.requireNonNull(args).getString("USERNAME")).getStatus();

            Toast.makeText(getContext(), tt.getUsername()+" pub key "+tt.getStatus(), Toast.LENGTH_LONG).show();
            Log.w("[GRACE]", Objects.requireNonNull(args).getString("USERNAME")+" setStatus = " + t);

            if (tt.getStatus().equals("SEND_PING")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getStatus().equals("WAIT_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getStatus().equals("SEND_PONG")) {
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

    public PublicKey getPublicKey(String username) {
        Log.w("------getPublicKey", ""+db.userDao().getUser(username).toString());
        String pK = db.userDao().getUser(username).getPublicKey();
        Log.e("-----", "*** "+pK);
        Log.e("-----", "***2 "+db.userDao().getUser(username).getUsername());

        // Convert String public Key to PublicKey
        byte[] publicBytes = Base64.decode(pK.getBytes(), 0);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);

        KeyFactory keyFactory;
        PublicKey publicKey = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert publicKey != null;
        Log.e("-----", "String to PublicKey");
        Log.w("-----", "[ getAlgorithm ]" + publicKey.getAlgorithm());
        Log.w("-----", "[ getFormat ]" + publicKey.getFormat());
        Log.w("-----", "[ getEncoded ]" + Arrays.toString(publicKey.getEncoded()));

        return publicKey;
    }

    public SecretKey generateSecretKey() {
        // DONE : Generation de la Clef Secrete
        KeyGenerator generator;
        SecretKey secretKey = null;

        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            secretKey = generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert secretKey != null;

        Log.e("-----", "Genere SecretKey");
        Log.w("-----", "[ getAlgorithm ]" + secretKey.getAlgorithm());
        Log.w("-----", "[ getFormat ]" + secretKey.getFormat());
        Log.w("-----", "[ getEncoded ]" + Arrays.toString(secretKey.getEncoded()));

        return secretKey;
    }


    public String chiffrer(byte[] secKBytes, PublicKey publicKey) {
        // DONE : ENCODE
        String chiffree;
        byte[] encodeTxt = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encodeTxt = cipher.doFinal(secKBytes);
            Log.i("[ENCODE]", Arrays.toString(encodeTxt));
            Log.i("[ENCODE]", new String(encodeTxt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        chiffree = new String(encodeTxt);

        return Arrays.toString(encodeTxt);
    }

    @SuppressLint("StaticFieldLeak")
    public void sendPong(final String username) {

        PublicKey publicKey = getPublicKey(username);
        Log.e("-----", "String to PublicKey");


        final SecretKey secretKey = generateSecretKey();
        Log.e("-----", "secretKey\n"+secretKey);

        final byte[] secKBytes = Base64.encode(secretKey.getEncoded(), 0);
        final String secretK = new String(secKBytes);
        Log.i("-----", "-----\n-----BEGIN SECRET KEY-----\n" + secretK + "-----END SECRET KEY-----\n");
        Log.i("-----", "-----\n-----BEGIN SECRET KEY-----\n" + secKBytes + "-----END SECRET KEY-----\n");

        //db.userDao().updateUserAes(username, Arrays.toString(secretKey.getEncoded()));
//        db.userDao().updateUserAes(username, new String(secKBytes));

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                db.userDao().updateUserAes(username, Arrays.toString(secretKey.getEncoded()));
                Log.w("------sendPong", ""+db.userDao().getUser(username).toString());
            }
        };
        handler.postDelayed(runnable, 1000);

        String res = chiffrer(secKBytes, publicKey);

        Log.w("CCCCCCCC", ""+res.length()+"\n"+Arrays.toString(secretKey.getEncoded()));
        Log.w("send", ""+ res);

        requestCreateMsg(username, "PONG[|]"+res);
    }


    public void sendMsg() {
        if (!Objects.equals(editSms.getText().toString(), "")) {
            if (!Objects.equals(res.getText().toString(), "")) {
                saveLocalNewMsg(res.getText().toString(), editSms.getText().toString(), false, true);

                // TODO : send server

                requestCreateMsg(res.getText().toString(), "MSG[|]" + editSms.getText().toString());
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
                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                db.userDao().updateUserStatus(receiver, "SECURE");
                                Log.w("------UPDATE STATUS", ""+db.userDao().getUser(receiver).toString());
                            }
                        };
                        handler.postDelayed(runnable, 1000);
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
        if (!Objects.equals(bundle.getString("MESSAGES"), "")) {
            Log.e("MESSAGES", bundle.getString("USERNAME")+"*****"+bundle.getString("MESSAGES"));
            try {
                //Log.w("MESSAGES", "*****"+db.messageDao().loadMessageForMsgUser(bundle.getString("USERNAME")));
                viewModel = ViewModelProviders.of(this, new MyViewModelFactory(this.getActivity().getApplication(), bundle.getString("USERNAME"))).get(MessageViewModel.class);
                viewModel.getMessageList().observe(this, new Observer<List<Message>>() {
                    @Override
                    public void onChanged(@Nullable List<Message> messages) {
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
        }

        /*try {

            assert getArguments() != null;
            editSms.setText(getArguments().getString("TXT_SHARED"));

            User tt = db.userDao().getUser(bundle.getString("USERNAME"));

            String t = db.userDao().getUser(bundle.getString("USERNAME")).getStatus();

            Toast.makeText(getContext(), tt.getUsername()+" pub key "+tt.getStatus(), Toast.LENGTH_LONG).show();
            Log.w("[GRACE]", bundle.getString("USERNAME")+" setStatus = " + t);

            if (tt.getStatus().equals("SEND_PING")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getStatus().equals("WAIT_PONG")) {
                Objects.requireNonNull(getView()).findViewById(R.id.chat_ping_bis).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.chat_pong_bis).setVisibility(View.GONE);

                Objects.requireNonNull(getView()).findViewById(R.id.chat_ll).setVisibility(View.GONE);
                Objects.requireNonNull(getView()).findViewById(R.id.layout_edt_sms).setVisibility(View.GONE);
            }

            else if (tt.getStatus().equals("SEND_PONG")) {
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
        }*/
    }
}
