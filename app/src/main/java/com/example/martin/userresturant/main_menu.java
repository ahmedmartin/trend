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
import java.util.Collections;

public class main_menu extends AppCompatActivity {

    private DatabaseReference myRef;

    private ListView part_list ;

    private ArrayList<String> parts= new ArrayList();
    private ArrayAdapter<String> adapter;

    private String type;
    private String kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Bundle bb= getIntent().getExtras();
       type= bb.getString("type");
       kind=bb.getString("kind");

        myRef= FirebaseDatabase.getInstance().getReference().child(kind).child(kind_list.kind_name).child("menu");

        part_list = findViewById(R.id.part_list);

        adapter = new ArrayAdapter<String>(this,R.layout.single_word_show,R.id.row,parts);
        part_list.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    i++;
                    parts.add(data.getKey().toString()); // get key value for tables added to tables array
                    Collections.sort(parts);   // sort tables array
                    adapter.notifyDataSetChanged(); // update data in table list
                }
                if(dataSnapshot.getChildrenCount()==i)
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        part_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent food = new Intent(main_menu.this,food_menu.class);
                Bundle b = new Bundle();
                b.putString("food_name",parts.get(position));
                b.putString("type",type);
                b.putString("kind",kind);
                food.putExtras(b);
                // finish();
                startActivity(food);
            }
        });

    }

   /* @Override
    public void onBackPressed() {
        Intent main= new Intent(main_menu.this,Main.class);
        startActivity(main);
        finish();
    }*/


}
