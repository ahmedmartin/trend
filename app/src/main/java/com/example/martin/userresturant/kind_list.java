package com.example.martin.userresturant;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class kind_list extends AppCompatActivity {

   public static String kind_name;

    private DrawerLayout dl ;
    private ActionBarDrawerToggle abdt;

    private ListView kind_list;
    private ArrayList <String> kinds_name = new ArrayList<>();
    private ArrayList <Uri> images = new ArrayList<>();

    private resturant_adapt adapt;

    private DatabaseReference ref ;

    private String kind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kind_list);


        kind =getIntent().getStringExtra("kind");

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

        get_kind_name();
        adapt = new resturant_adapt(kind_list.this, kinds_name,images);
        kind_list = findViewById(R.id.resturant_list);
        kind_list.setAdapter(adapt);
        kind_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent main = new Intent(kind_list.this,Main.class);
                kind_name = kinds_name.get(position);
                main.putExtra("kind",kind);
                startActivity(main);
            }
        });


    }


    void get_kind_name(){
        ref = FirebaseDatabase.getInstance().getReference().child(kind);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int i=0;
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        i++;
                        kinds_name.add(data.getKey().toString());
                    }
                    if (i==dataSnapshot.getChildrenCount()){
                        photos(0);
                        ref.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void photos(final int i){
        StorageReference stor = FirebaseStorage.getInstance().getReference().child(kinds_name.get(i)+".png");
            stor.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                public void onSuccess(Uri uri) {
                    images.add(uri);
                    adapt.notifyDataSetChanged();
                    if(images.size()-1!= kinds_name.size()-1){
                        photos(images.size());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    Toast.makeText(kind_list.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void personal_info() {
        Intent person=new Intent(kind_list.this,person_info.class);
        startActivity(person);
    }

    public  void delivery_activity (){
        Intent deliver = new Intent(kind_list.this,delivery.class);
        startActivity(deliver);
    }

    public void table_activity(){
        Intent table =new Intent(kind_list.this, table.class);
        startActivity(table);
    }

    public void exit() {
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser carrent =mauth .getCurrentUser();
        mauth.signOut();
        Intent sign_in=new Intent(kind_list.this,sign_in.class);
        sign_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(sign_in);
    }



}
