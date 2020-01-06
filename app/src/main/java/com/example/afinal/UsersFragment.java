package com.example.afinal;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.afinal.adapters.AdapterUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;

    FirebaseAuth firebaseAuth;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth=FirebaseAuth.getInstance();

        recyclerView=view.findViewById(R.id.users_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usersList=new ArrayList<>();

        getAllUsers();


  return view;
    }

    private void getAllUsers() {
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
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
                adapterUsers=new AdapterUsers(getActivity(),usersList);
                recyclerView.setAdapter(adapterUsers);

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUsers(final String query) {
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUsers modelUsers=ds.getValue(ModelUsers.class);

                    if (!modelUsers.getUid().equals(fUser.getUid())){

                       if (modelUsers.getName().toLowerCase().contains(query.toLowerCase()) ||
                        modelUsers.getEmail().toLowerCase().contains(query.toLowerCase()))
                       {

                           usersList.add(modelUsers);
                       }

                    }
                    adapterUsers=new AdapterUsers(getActivity(),usersList);

                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user !=null)
        {

        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
           getActivity().finish();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu optoin in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        //hide addpost icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);

//SEARCH VIEW
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

              if (!TextUtils.isEmpty(s.trim())){

                  //search text contains text,search it
                  searchUsers(s);
              }
              else {
                  //search text empty,get all users
                  getAllUsers();
              }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())){

                    //search text contains text,search it
                    searchUsers(s);
                }
                else {
                    //search text empty,get all users
                    getAllUsers();
                }
                return false;
            }
        });





         super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}
