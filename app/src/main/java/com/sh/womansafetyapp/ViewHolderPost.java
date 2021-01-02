package com.sh.womansafetyapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

public class ViewHolderPost extends RecyclerView.ViewHolder {
    View mview ;
    TextView nameS,phoneS;
    ImageView delete;
    DatabaseReference ref;


    public ViewHolderPost(@NonNull View itemView) {
        super(itemView);

      nameS=itemView.findViewById(R.id.name);
        phoneS=itemView.findViewById(R.id.phone);
        delete=itemView.findViewById(R.id.delete);





    }
    public void setDetails(String name,String phone) {

        // final String r=refa;

        nameS.setText(name);
        phoneS.setText(phone);











    }




}
