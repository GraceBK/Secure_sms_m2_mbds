package fr.mbds.securesms.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.mbds.securesms.R;
import fr.mbds.securesms.db.room_db.Message;

public class MyMsgAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<>();
    Context context;

    public MyMsgAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    public void addManyMassage(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void clear() {
        this.messages.clear();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isCurrentUser()) {
            view = inflater.inflate(R.layout.item_my_sms, null);
            holder.messageBody = view.findViewById(R.id.item_msg_body);
            holder.messageDate = view.findViewById(R.id.item_msg_date);
            view.setTag(holder);
            holder.messageBody.setText(message.getMessage());
            holder.messageDate.setText(message.getDateCreated());
        } else {
            view = inflater.inflate(R.layout.item_their_sms, null);
            holder.messageBody = view.findViewById(R.id.item_msg_body);
            holder.messageDate = view.findViewById(R.id.item_msg_date);
            view.setTag(holder);

            holder.messageBody.setText(message.getMessage());
            holder.messageDate.setText(message.getDateCreated());

        }

        return view;
    }


    class MessageViewHolder {
        public TextView username;
        public TextView messageBody;
        public TextView messageDate;
    }
}
