package fr.mbds.securesms.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.mbds.securesms.R;
import fr.mbds.securesms.models.User;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyViewHolder> {

    private ArrayList<User> userArrayList;

    public MyUserAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
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
        User user = userArrayList.get(i);
        myViewHolder.username.setText(user.getUsername());
        myViewHolder.shortResume.setText(user.getResume());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public TextView username;
        public TextView shortResume;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.item_ll);
            username = itemView.findViewById(R.id.item_list_username);
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
