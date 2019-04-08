package com.example.martin.userresturant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class food_details extends AppCompatActivity {

    private ImageView food_img;
    private TextView  description;
    private TextView  tx_size;
    private TextView  addition_title;
    private TextView  addition_item;
    private TextView  tx_count;
    private Button btn_delivery;
    private Button btn_table;


   // private ListView size_list;
    private ArrayList <String> sizes= new ArrayList<>();
    private ArrayList <Double> pricies =new ArrayList<>();
   // private CustomListAdapter adapter ;
    private ArrayList <String> addition_name=new ArrayList<>();
    private ArrayList <Double> addition_pricies=new ArrayList<>();

    private ArrayList<String> [] choose_array;

    private String [] item_choosed;
    private ArrayList <TextView> text = new ArrayList<>();

   LinearLayout linearLayout;

    private String state;
    private String type;
    private String part;
    private String food_name;
    private String size;
    private int order_number=0;
    private Double price;
    private String uid;
    private String kind;




    private DatabaseReference myRef;
    private DatabaseReference details_ref;
    private DatabaseReference choose_ref;
    private DatabaseReference addition_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        type =getIntent().getStringExtra("type");
        part = getIntent().getStringExtra("part");
        food_name = getIntent().getStringExtra("food name");
        kind=getIntent().getStringExtra("kind");

        linearLayout =findViewById(R.id.linear_layout);
        tx_size=findViewById(R.id.size);
        addition_item =findViewById(R.id.addition_item);
        addition_title = findViewById(R.id.addition_title);
        tx_count =findViewById(R.id.count);
        btn_delivery=findViewById(R.id.delivery);
        btn_table=findViewById(R.id.table);

        if(! TextUtils.isEmpty(sign_in.name_thing_in_kind)) {
            final DatabaseReference stat = FirebaseDatabase.getInstance().getReference().child(kind).child(sign_in.name_thing_in_kind)
                    .child("part").child(sign_in.part_table).child("table").child(sign_in.table_number).child("state");
            stat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        state = dataSnapshot.getValue().toString();
                    stat.removeEventListener(this);
                }
                public void onCancelled(DatabaseError databaseError) { }
            });
        }

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        details_ref =FirebaseDatabase.getInstance().getReference().child(kind).child(kind_list.kind_name)
                     .child("menu").child(part).child(food_name);
        details_ref .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    i++;
                    if(data.getKey().toString().equals("description")){
                        description.setText(data.getValue().toString());
                    }else if(data.getKey().toString().equals("size")){
                        for(DataSnapshot date : data.getChildren()) {
                            sizes.add(date.getKey().toString());
                            pricies.add(Double.parseDouble(date.getValue().toString()));
                        }
                        if(data.getChildrenCount()==1){
                           size=sizes.get(0);
                           price=pricies.get(0);
                           tx_size.setText(size+"\t"+price);
                        }
                    }
                }
                /*if(i==dataSnapshot.getChildrenCount()){
                    adapter.notifyDataSetChanged();
                }*/
                details_ref.removeEventListener(this);
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        choose_ref =details_ref.child("choose");
        choose_ref.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    choose_array = new ArrayList[(int) dataSnapshot.getChildrenCount()];
                    item_choosed = new String[(int) dataSnapshot.getChildrenCount()];
                    int i = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        choose_array[i] = new ArrayList<String>();
                        for (DataSnapshot ds : data.getChildren()) {
                            choose_array[i].add(ds.getKey().toString());
                        }
                        generate_textview(i,data);
                        i++;
                    }
                }
                choose_ref.removeEventListener(this);
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        addition_ref=details_ref.child("addition");
        addition_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    addition_item.setVisibility(View.VISIBLE);
                    addition_title.setVisibility(View.VISIBLE);
                    for(DataSnapshot dd :dataSnapshot.getChildren()) {
                        addition_title.setText(dd.getKey().toString());
                        for (DataSnapshot data : dd.getChildren()) {
                            addition_name.add(data.getKey().toString());
                            addition_pricies.add(Double.parseDouble(data.getValue().toString()));
                        }
                    }
                }
                addition_ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final StorageReference food_img_ref = FirebaseStorage.getInstance().getReference()
                .child(kind_list.kind_name).child(part).child(food_name+".png");
        food_img_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(food_details.this).load(uri).into(food_img);
            }
        });

        food_img =findViewById(R.id.food_img);
        description =findViewById(R.id.description);


    }






    private void generate_textview(final int position, DataSnapshot data) {
        TextView t=new TextView(food_details.this);
        t.setText(data.getKey().toString());
        t.setTextSize(25);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params  =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,40,0,0);
        t.setLayoutParams(params);
        linearLayout.addView(t,4);

        TextView tt=new TextView(food_details.this);
        tt.setText("required");
        tt.setTextSize(20);
        tt.setTextColor(Color.BLACK);
        linearLayout.addView(tt,5);
        tt.setId(position);
        tt.setGravity(Gravity.CENTER);
        text.add(tt);
        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final RadioGroup group = new RadioGroup(food_details.this);
                for(int i=0;i<choose_array[v.getId()].size();i++){
                    RadioButton rb =new RadioButton(food_details.this);
                    rb.setText(choose_array[v.getId()].get(i));
                    rb.setId(i);
                    rb.setTextSize(20);
                    group.addView(rb);
                }
                AlertDialog.Builder alart = new AlertDialog.Builder(food_details.this);
                alart.setMessage("enter your choice");
                alart.setView(group);
                alart.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       int i= group.getCheckedRadioButtonId();
                        RadioButton rb = group.findViewById(i);
                        if(rb!=null) {
                            TextView t = (TextView) v;
                            item_choosed[v.getId()] = rb.getText().toString();
                            t.setText(rb.getText().toString());
                        }
                    }
                });
               alart.show();

            }
        });
    }



    private void set_order_number() {
        String food = food_name+" "+size;
        for(int i=0;i<text.size();i++){
            food+=" "+text.get(i).getText().toString();
        }
        if(! addition_item.getText().equals("option")){
            food+="   add     ";
            String s []=addition_item.getText().toString().split(",");
            for(int i=0;i<s.length;i++){
                String [] ss = s[i].split("__");
                food+=ss[0]+" ";
                price+=Double.parseDouble(ss[1]);
            }
        }

        if(type.equals("table")){
            myRef= FirebaseDatabase.getInstance().getReference().child(kind)
                    .child(sign_in.name_thing_in_kind).child("part").child(sign_in.part_table)
                    .child("table").child(sign_in.table_number).child("unread").child(food);

            btn_table.setVisibility(View.VISIBLE);
        }else {
            myRef = FirebaseDatabase.getInstance().getReference().child("trend")
                    .child("unsend").child(uid).child(kind_list.kind_name).child(food);

            btn_delivery.setVisibility(View.VISIBLE);
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String s=dataSnapshot.getValue().toString();
                    String a[] = s.split("x");
                    order_number+=Integer.parseInt(a[1]);
                    myRef.setValue(price+"x"+order_number);
                    Toast.makeText(food_details.this,"تم اضافته الى الكيس ",Toast.LENGTH_SHORT).show();
                }else{
                    myRef.setValue(price+"x"+order_number);
                    Toast.makeText(food_details.this,"تم اضافته الى الكيس ",Toast.LENGTH_SHORT).show();
                }

                myRef.removeEventListener(this);
            }
            public void onCancelled(DatabaseError databaseError) {}
        });


    }



    public void add(View view) {
        if ((type.equals("delivery")) || (type.equals("table") && (state.equals("جارى الطلب") || state.equals("طعام مضاف")))) {
           if( check_fields()) {
               if (TextUtils.isEmpty(tx_count.getText().toString())) {
                   Toast.makeText(food_details.this, "enter count", Toast.LENGTH_SHORT).show();
               } else {
                   order_number = Integer.parseInt(tx_count.getText().toString());
                   set_order_number();
               }
           }
        }else
            Toast.makeText(food_details.this,"يتم الان "+state,Toast.LENGTH_SHORT).show();
    }

    private boolean check_fields() {

        if(TextUtils.isEmpty(size)) {
            tx_size.callOnClick();
            return false;
        }

        for(int i=0;i<text.size();i++){
            if(text.get(i).getText().equals("required")) {
                text.get(i).callOnClick();
                return false;
            }

        }
        return true;
    }

    public void get_size(View view) {
        if(sizes.size()>1){
            final RadioGroup group =new RadioGroup(food_details.this);

            for(int i=0;i<sizes.size();i++){
                RadioButton rb =new RadioButton(food_details.this);
                rb.setText(sizes.get(i)+"\n"+pricies.get(i));
                rb.setId(i);
                rb.setTextSize(20);
                group.addView(rb);
            }
            AlertDialog.Builder alart = new AlertDialog.Builder(food_details.this);
            alart.setMessage("enter your size");
            alart.setView(group);
            alart.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int i = group.getCheckedRadioButtonId();
                    RadioButton rb = group.findViewById(i);
                    if(rb!=null) {
                        size=sizes.get(i);
                        price=pricies.get(i);
                        tx_size.setText(size+"\t"+price);
                    }
                }
            });
            alart.show();
        }

    }

    public void get_addition(View view) {
        final LinearLayout layout =new LinearLayout(food_details.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        for(int i =0;i<addition_name.size();i++){
            CheckBox box =new CheckBox(food_details.this);
            box.setText(addition_name.get(i)+"\n"+addition_pricies.get(i));
            box.setTextSize(20);
            box.setId(i);
            layout.addView(box);
        }
        AlertDialog.Builder alart = new AlertDialog.Builder(food_details.this);
        alart.setMessage("enter your "+addition_title.getText());
        alart.setView(layout);
        alart.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s ="" ;
                boolean have_checked = false;
                for(int i=0;i<addition_name.size();i++){
                    CheckBox box =layout.findViewById(i);
                    if(box.isChecked()) {
                        s += addition_name.get(i) + "__" + addition_pricies.get(i) + ",";
                        have_checked=true;
                    }
                }
                if(have_checked)
                    addition_item.setText(s.substring(0,s.length()-1));
                else
                    addition_item.setText("option");
            }
        });
        alart.show();
    }

    public void increase(View view) {
       int cout = Integer.parseInt(tx_count.getText().toString());
       tx_count.setText(++cout+"");

    }

    public void decrease(View view) {
        int cout = Integer.parseInt(tx_count.getText().toString());
        if(cout>1){
            tx_count.setText(--cout+"");
        }
    }

    public void go_to_table(View view) {
        Intent table =new Intent(this, table.class);
        startActivity(table);
    }

    public void go_to_delivery(View view) {
        Intent delivery =new Intent(this, delivery.class);
        startActivity(delivery);
    }

    @Override
    protected void onStop() {
        super.onStop();
        btn_delivery.setVisibility(View.INVISIBLE);
        btn_table.setVisibility(View.INVISIBLE);
    }
}
