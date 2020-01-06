package com.example.afinal;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class showPost extends AppCompatActivity {
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> post = new ArrayList<>();
    ArrayList<Integer> image = new ArrayList<>();
    ListView listView;

    FirebaseAuth firebaseAuth;

    List<ModelUsers> usersList;

    customAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        //firebaseAuth=FirebaseAuth.getInstance();
        //post
        firebaseAuth=FirebaseAuth.getInstance();
        usersList=new ArrayList<>();
        listView=findViewById(R.id.listView);



        //bundle=getInt
        getAllPost();
    }
    private void getAllPost() {
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            post.add(bundle.getString("post"));
        }
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUsers modelUsers=ds.getValue(ModelUsers.class);

                    if (!modelUsers.getUid().equals(fUser.getUid())){
                        usersList.add(modelUsers);
                    }
                    image.add(R.drawable.ic_profile_black);
                    name.add(modelUsers.name);
                   ;
                    //Toast.makeText(getContext(),"bal"+post,Toast.LENGTH_LONG).show();
                    adapter=new customAdapter(showPost.this,image,name,post);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(showPost.this,"bal"+name,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
