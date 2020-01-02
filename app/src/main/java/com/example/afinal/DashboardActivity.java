package com.example.afinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //actionbar and it's title
         actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");
        //enable back button
        /*actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/
        firebaseAuth=FirebaseAuth.getInstance();
        //bottom nevigation

        BottomNavigationView navigationView=findViewById(R.id. navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

//home fragment transaction
        actionBar.setTitle("Home");
        HomeFragment fragment1=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           //handle item clicks
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                   HomeFragment fragment1=new HomeFragment();
                   FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                   ft1.replace(R.id.content,fragment1,"");
                   ft1.commit();
                    return true;
                case R.id.nav_profile:
                  actionBar.setTitle("Profile");
                   ProfileFragment fragment2=new ProfileFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,fragment2,"");
                    ft2.commit();
                    return true;
                case R.id.nav_users:
                    actionBar.setTitle("Users");
                   UsersFragment fragment3=new UsersFragment();
                    FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,fragment3,"");
                    ft3.commit();
                    return true;
            }
            return false;
        }
    };
    private  void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user !=null)
        {

        }
        else {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


}
