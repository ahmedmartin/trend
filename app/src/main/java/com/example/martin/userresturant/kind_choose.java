package com.example.martin.userresturant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class kind_choose extends AppCompatActivity {

    private Intent kind;

    private DrawerLayout dl ;
    private ActionBarDrawerToggle abdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kind_choose);

        kind = new Intent(kind_choose.this,kind_list.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        dl =findViewById(R.id.draw_layout);
        abdt = new ActionBarDrawerToggle(this,dl,toolbar,R.string.open,R.string.close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav = findViewById(R.id.navigation);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id =item.getItemId();
                switch (id){
                    case R.id.table_order:
                        table_activity();
                        break;

                    case R.id.delivery_order :
                        delivery_activity();
                        break;

                    case R.id.person_info :
                        personal_info();
                        break;

                    case R.id.log_out :
                        exit();
                        break;
                }

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void personal_info() {
        Intent person=new Intent(kind_choose.this,person_info.class);
        startActivity(person);
    }

    public  void delivery_activity (){
        Intent deliver = new Intent(this,delivery.class);
        startActivity(deliver);
    }

    public void table_activity(){
        Intent table =new Intent(this, table.class);
        startActivity(table);
    }

    public void exit() {
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser carrent =mauth .getCurrentUser();
        mauth.signOut();
        Intent sign_in=new Intent(this,sign_in.class);
        sign_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(sign_in);
    }

    public void coffee(View view) {
        kind.putExtra("kind","coffee");
        startActivity(kind);
    }

    public void restaurant(View view) {
        kind.putExtra("kind","resturants");
        startActivity(kind);
    }

    @Override
    public void onBackPressed() {
        Intent sign_in=new Intent(this,sign_in.class);
        sign_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }
}
