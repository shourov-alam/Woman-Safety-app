package com.sh.womansafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.FileVisitResult;

public class Rescuer extends AppCompatActivity {



    RecyclerView recyclerView;
    LinearLayoutManager llm;
    public FirebaseRecyclerAdapter<Rescuer_Model, ViewHolderPost> firebaseRecyclerAdapter;
    public FirebaseRecyclerOptions<Rescuer_Model> options; // seraching in the profile ;
    DatabaseReference ref;


    Button btn;

    String a;
    EditText nam,phn;
    ProgressDialog pd;
    boolean test;
    SwipeRefreshLayout sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer);


        getSupportActionBar().setTitle("Rescuers Details");
        recyclerView = findViewById(R.id.myList);


        nam=findViewById(R.id.res_name);
        phn=findViewById(R.id.res_phone);
        btn=findViewById(R.id.enter);


        ref = FirebaseDatabase.getInstance().getReference("Rescuers");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() >= 5){

                            Toast.makeText(getApplicationContext(),"Maximum number of Rescuers added",Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(nam.getText().toString().isEmpty() || phn.getText().toString().isEmpty()){

                                Toast.makeText(getApplicationContext(),"Enter both name & phone",Toast.LENGTH_LONG).show();
                            }else {

                                Rescuer_Model rescuer_model=new Rescuer_Model(nam.getText().toString(),phn.getText().toString());

                                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ref.push().getKey()).setValue(rescuer_model);

                            }


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });




        llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);




        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {

            loadData();

        }else {


            Toast.makeText(getApplicationContext(),"User Not Found",Toast.LENGTH_LONG).show();
        }






    }


    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            firebaseRecyclerAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            firebaseRecyclerAdapter.startListening();
        }
    }


    public void loadData() {

        //  String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

       // Query fireBaseQusery  = ref.orderByChild("Rescuers").equalTo(FirebaseAuth.getInstance().getUid()) ;



        options = new FirebaseRecyclerOptions.Builder<Rescuer_Model>().setQuery(ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()) , Rescuer_Model.class).build() ;

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Rescuer_Model, ViewHolderPost>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolderPost viewHolderPost, final  int i, @NonNull final Rescuer_Model rescuer_model) {



                // setThe data to the row
                //        String imageLink , String itemdes ,String name  ,String quatitys  ,String  category ;


               // Toast.makeText(getApplicationContext(),rescuer_model.getName(),Toast.LENGTH_LONG).show();
                //     String name , String quantity , String mail , String returnDate , String stats ;
                viewHolderPost.setDetails(rescuer_model.getName(),rescuer_model.getPhone()






                );

                viewHolderPost.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getRef(viewHolderPost.getAdapterPosition()).getKey()).removeValue();
                        //notifyItemRemoved(i);
                       // notifyDataSetChanged();
                    }
                });


               /* viewHolderPost.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ref.child(getRef(i).getKey()).removeValue();

                    }
                });

                viewholderForItemList.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        LayoutInflater layoutInflater = LayoutInflater.from(User_Posts.this);

                        final View view = layoutInflater.inflate(R.layout.add_post, null);
                        final EditText title=view.findViewById(R.id.title);
                        final EditText des=view.findViewById(R.id.des);

                        title.setText(post_model.getTitle());
                        des.setText(post_model.getDes());
                        new AlertDialog.Builder(User_Posts.this)


                                .setCancelable(true)
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {







                                        ref.child(getRef(i).getKey()).child("title").setValue(title.getText().toString());
                                        ref.child(getRef(i).getKey()).child("des").setValue(des.getText().toString());


                                    }
                                }).setView(view).show();




                    }
                });
*/











            }





            @NonNull
            @Override
            public ViewHolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rescuer_item, parent, false);
                ViewHolderPost viewHolder = new ViewHolderPost(itemView);

                return viewHolder;
            }
        } ;

        recyclerView.setLayoutManager(llm);

        firebaseRecyclerAdapter.startListening();

        //setting adapter

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}

