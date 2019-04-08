package com.example.martin.userresturant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class food_menu extends AppCompatActivity {

    private DatabaseReference myRef;
    private DatabaseReference stat;

    private ListView food_list;


    private ArrayAdapter <String> custom;
    private ArrayList<String> foods=new ArrayList<>();


    private String part;
    private String type;
    private String kind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);



        Bundle b = getIntent().getExtras();
        part =  b.getString("food_name");
        type = b.getString("type");
        kind=b.getString("kind");



        food_list =findViewById(R.id.food_list);

        myRef = FirebaseDatabase.getInstance().getReference().child(kind).child(kind_list.kind_name).child("menu").child(part);



        custom= new ArrayAdapter<>(food_menu.this,R.layout.single_word_show,R.id.row,foods);
        food_list.setAdapter(custom);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    i++;
                    foods.add(data.getKey().toString());
                    custom.notifyDataSetChanged();
                }
                if(i==dataSnapshot.getChildrenCount())
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        food_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent food_details =new Intent(food_menu.this, food_details.class);
                food_details.putExtra("part",part);
                food_details.putExtra("type",type);
                food_details.putExtra("food name",foods.get(position));
                food_details.putExtra("kind",kind);
                startActivity(food_details);
            }
        });


    }





}
