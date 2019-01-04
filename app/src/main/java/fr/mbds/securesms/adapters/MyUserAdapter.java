package fr.mbds.securesms.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.mbds.securesms.R;
import fr.mbds.securesms.db.room_db.User;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyViewHolder> {

    private List<User> userList;

    public MyUserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    /**
     * On Recharge les cellules
     * @param myViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        User user = userList.get(i);
        myViewHolder.username.setText(user.getUsername());

        String[] color = user.getThumbnail().split("-");
        int alpha = 70;

        myViewHolder.thumbnail.setBackgroundColor(Color.argb(alpha, Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
        alpha = 100;
        myViewHolder.firstLetterName.setTextColor(Color.argb(alpha, Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
        myViewHolder.firstLetterName.setText(user.getUsername().substring(0, 1).toUpperCase());

        if (user.getIdPubKey().equals("WAIT_PONG")) {
            /* ALICE [PAGE 1] --ping--> BOB [] */
            myViewHolder.imgSecure.setBackgroundResource(R.drawable.ic_lock_open);
            myViewHolder.shortResume.setText("En attent d'un PONG");

        } else if (user.getIdPubKey().equals("SEND_PING_BIS")) {
            /* ALICE [PAGE 1 bis] */
            myViewHolder.imgSecure.setBackgroundResource(R.drawable.ic_lock_open);
            myViewHolder.shortResume.setText("Cle public envoyé");

        } else if (user.getIdPubKey().equals("SEND_PONG")) {
            /* ALICE [PAGE 1 bis] <--pong-- BOB [PAGE 2] */
            myViewHolder.imgSecure.setBackgroundResource(R.drawable.ic_lock_open);
            myViewHolder.shortResume.setText("Envoyer votre cle");

        } else if (user.getIdPubKey().equals("SEND_PONG_BIS")) {
            /* ALICE [PAGE 2 bis] ---- BOB [PAGE 2 bis] */
            myViewHolder.imgSecure.setBackgroundResource(R.drawable.ic_lock_open);
            myViewHolder.shortResume.setText("");

        } else {// PONG_RECEIVE
            myViewHolder.imgSecure.setBackgroundResource(R.drawable.ic_lock);
            myViewHolder.shortResume.setText("Chat secure");
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public void updatePersonneList(List<User> personnes) {
        this.userList = personnes;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public TextView username;
        public TextView shortResume;
        public ImageView thumbnail;
        public TextView firstLetterName;
        public ImageView imgSecure;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.item_ll);
            username = itemView.findViewById(R.id.item_list_username);
            thumbnail = itemView.findViewById(R.id.item_list_img);
            firstLetterName = itemView.findViewById(R.id.item_list_img_txt);
            imgSecure = itemView.findViewById(R.id.id_sms_is_secure);
            shortResume = itemView.findViewById(R.id.item_list_resume);

            /*linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(), "COUCOU "+username.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

}
