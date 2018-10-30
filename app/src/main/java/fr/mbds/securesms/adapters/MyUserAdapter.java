package fr.mbds.securesms.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.mbds.securesms.R;
import fr.mbds.securesms.db.room_db.Personnes;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyViewHolder> {

    private List<Personnes> personnesList;

    public MyUserAdapter(List<Personnes> personnesList) {
        this.personnesList = personnesList;
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
        Personnes personnes = personnesList.get(i);
        myViewHolder.username.setText(personnes.getUsername());
        //myViewHolder.mail.setText(personnes.getMail());
    }

    @Override
    public int getItemCount() {
        return personnesList.size();
    }


    public void updatePersonneList(List<Personnes> personnes) {
        this.personnesList = personnes;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public TextView username;
        public TextView shortResume;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.item_ll);
            username = itemView.findViewById(R.id.item_list_username);
            //shortResume = itemView.findViewById(R.id.item_list_resume);

            /*linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(), "COUCOU "+username.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

}
