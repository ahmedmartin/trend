package com.example.martin.userresturant;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity {

    private DrawerLayout dl ;
    private ActionBarDrawerToggle abdt;

    private String kind;
    private String state;
    private String part_booked;
    private String uid;

    private CircleImageView delivery;
    private CircleImageView table;
    private LinearLayout option_layout;
    private LinearLayout booked_layout;
    private LinearLayout select_layout;
    private TimePicker picker;
    private TextView count;
    private Spinner part_spin;

    private ArrayList<String>part_list=new ArrayList<>();
    private ArrayAdapter<String>adapt ;



    @Override
    protected void onStart() {
        super.onStart();
        cancel_ref = FirebaseDatabase.getInstance().getReference().child(kind).child(sign_in.name_thing_in_kind)
                .child("part").child(sign_in.part_table).child("table").child(sign_in.table_number).child("state");

        check_state();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kind =getIntent().getStringExtra("kind");
        delivery =findViewById(R.id.delivery);
        table =findViewById(R.id.table);
        option_layout=findViewById(R.id.option_layout);
        booked_layout=findViewById(R.id.booked_layout);
        select_layout=findViewById(R.id.select_layout);
        count=findViewById(R.id.count);
        picker=findViewById(R.id.time_picker);
        picker.setIs24HourView(true);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        part_spin=findViewById(R.id.parts);
        adapt=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,part_list);
        adapt.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        part_spin.setAdapter(adapt);
        part_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                part_booked=part_list.get(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(kind).child(kind_list.kind_name);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String s =dataSnapshot.child("have_tables").getValue().toString();
                if( dataSnapshot.child("have_tables").getValue().toString().equals("false"))
                    table.setVisibility(View.INVISIBLE);

                if( dataSnapshot.child("have_delivery").getValue().toString().equals("false"))
                    delivery.setVisibility(View.INVISIBLE);

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        dl =findViewById(R.id.draw_layout);
        abdt = new ActionBarDrawerToggle(this,dl,toolbar,R.string.open,R.string.close);
        abdt.setDrawerIndicatorEnabled(true);

        table_details();

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
        Intent person=new Intent(Main.this,person_info.class);
        startActivity(person);
    }

    public void delivery(View view) {
        Intent main_menu = new Intent(Main.this, main_menu.class);
        Bundle b = new Bundle();
        b.putString("type","delivery");
        b.putString("kind",kind);
        main_menu.putExtras(b);
        startActivity(main_menu);


    }

    public  void delivery_activity (){
        Intent deliver = new Intent(Main.this,delivery.class);
        startActivity(deliver);
    }

    public void table_activity(){
        Intent table =new Intent(Main.this, table.class);
        startActivity(table);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cancel_ref!=null&&v_listen!=null)
        cancel_ref.removeEventListener(v_listen);
    }

    private boolean yes=true;
    public void table(View view) {
        if(yes) {
            option_layout.setVisibility(View.VISIBLE);
            yes = false;
        }else{
            option_layout.setVisibility(View.INVISIBLE);
            yes = true;
        }

    }

    private void check_state() {
        final DatabaseReference myref= FirebaseDatabase.getInstance().getReference().child(kind).child(sign_in.name_thing_in_kind)
                .child("part").child(sign_in.part_table).child("table").child(sign_in.table_number).child("state");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                state=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ValueEventListener v_listen;
    private  DatabaseReference cancel_ref;
    private void canceled_happend(){


       v_listen= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(! dataSnapshot.exists()){
                    Intent kind_choose=new Intent(Main.this,kind_choose.class);
                    kind_choose.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(kind_choose);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        cancel_ref.addValueEventListener(v_listen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null)
                Toast.makeText(this, "scanner canceled", Toast.LENGTH_SHORT).show();
            else{

                // get restaurant name , part , table number and if error occur this code is not valid
                try {
                    final String s = result.getContents();
                    String[] ss = s.split("-");
                    sign_in.name_thing_in_kind = ss[0];
                    sign_in.part_table = ss[1];
                    sign_in.table_number = ss[2];

                    // check if this table booked or not
                    final DatabaseReference myref= FirebaseDatabase.getInstance().getReference().child(kind)
                            .child(sign_in.name_thing_in_kind).child("part").child(sign_in.part_table).child("table").child(sign_in.table_number);
                    myref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){   // if i found this table booked before cancel every thing
                                Toast.makeText(Main.this, "this table booked"+"\n please try another one ", Toast.LENGTH_LONG).show();
                                sign_in.name_thing_in_kind = "";
                                sign_in.table_number ="";
                                sign_in.part_table="";
                            }else {      // if not found put details and ready for add food
                                if(sign_in.name_thing_in_kind.equals(kind_list.kind_name)) {
                                    myref.child("state").setValue("جارى الطلب");  // user state for  restaurant
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                                    myref.child("uid").setValue(uid);  // uid for restaurant
                                    state="جارى الطلب";

                                    // put table details to user to know if he has table and it's info
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                                    ref.child("table details").setValue(s);
                                }else{
                                    sign_in.name_thing_in_kind = "";
                                    sign_in.table_number ="";
                                    sign_in.part_table="";
                                    Toast.makeText(Main.this, "this code not valid with restaurant"+"\n choose correct restaurant from main list", Toast.LENGTH_LONG).show();
                                }
                            }
                            myref.removeEventListener(this);
                        }
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }catch (Exception e){
                    sign_in.name_thing_in_kind = "";
                    sign_in.table_number ="";
                    sign_in.part_table="";
                    Toast.makeText(this, " should scan code in restaurant "+"\n this code not valid", Toast.LENGTH_LONG).show();
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void exit() {
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        mauth.signOut();
        Intent sign_in=new Intent(Main.this,sign_in.class);
        sign_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(sign_in);
    }

    void table_details(){
        final DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("table details");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String s = dataSnapshot.getValue().toString();
                    try {
                        String[] ss = s.split("-");
                        sign_in.name_thing_in_kind = ss[0];
                        sign_in.part_table = ss[1];
                        sign_in.table_number = ss[2];
                    }catch (Exception e){
                        sign_in.name_thing_in_kind = "";
                        sign_in.table_number ="";
                        sign_in.part_table="";
                    }
                }else{
                    mref.setValue(" ");
                }
                mref.removeEventListener(this);
            }
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    private boolean book_mode ;
    private DatabaseReference user_book_ref;
    public void booked(View view) {
       user_book_ref =FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("table_booked");
        user_book_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (! TextUtils.isEmpty(dataSnapshot.getValue().toString())) {
                        Toast.makeText(Main.this, "you booked before ", Toast.LENGTH_LONG).show();
                    }else
                        open_book_layout();
                }else {
                    user_book_ref.setValue("");
                    open_book_layout();
                }
                user_book_ref.removeEventListener(this);
            }

            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void get_parts(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("resturants")
                .child(kind_list.kind_name).child("parts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    part_list.add(data.getKey().toString());
                    adapt.notifyDataSetChanged();
                }
                ref.removeEventListener(this);
            }

            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void open_book_layout() {
        book_mode = true;
        select_layout.setVisibility(View.INVISIBLE);
        select_layout.setEnabled(false);
        booked_layout.setEnabled(true);
        booked_layout.setVisibility(View.VISIBLE);
        get_parts();
    }


    public void scan(View view) {

        if(TextUtils.isEmpty(sign_in.name_thing_in_kind)) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(Main.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            intentIntegrator.setPrompt("Scan");
            intentIntegrator.setCameraId(0);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setBarcodeImageEnabled(false);
            intentIntegrator.initiateScan();
        }else {
            canceled_happend();
            if(state.equals("غلق الطاوله"))
                Toast.makeText(Main.this, "table is closed you can't add food", Toast.LENGTH_LONG).show();
            else {

                if (sign_in.name_thing_in_kind.equals(kind_list.kind_name)) {
                    Intent main_menu = new Intent(Main.this, main_menu.class);
                    Bundle b = new Bundle();
                    b.putString("type", "table");
                    b.putString("kind", kind);
                    main_menu.putExtras(b);
                    startActivity(main_menu);
                } else
                    Toast.makeText(this, "should  choose food from booked restaurant ", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(book_mode){
            booked_layout.setEnabled(false);
            booked_layout.setVisibility(View.INVISIBLE);
            select_layout.setVisibility(View.VISIBLE);
            select_layout.setEnabled(true);
            book_mode=false;
        }else
        super.onBackPressed();
    }



    public void submit_book(View view) {
        String uid =FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference book = FirebaseDatabase.getInstance().getReference().child("resturants").child(kind_list.kind_name)
                .child("part").child(part_booked).child("booked").child(uid);
        book.child("people_number").setValue(count.getText().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            book.child("time").setValue( picker.getHour()+":"+picker.getMinute());
        }
        user_book_ref.setValue(kind_list.kind_name+"-"+part_booked);
        booked_layout.setEnabled(false);
        booked_layout.setVisibility(View.INVISIBLE);
        select_layout.setVisibility(View.VISIBLE);
        select_layout.setEnabled(true);
        book_mode=false;

    }


    public void increase(View view) {
        int c = Integer.parseInt(count.getText().toString());
        count.setText(++c+"");
    }

    public void decrease(View view) {
        int c = Integer.parseInt(count.getText().toString());
        if(c>1){
            count.setText(--c+"");
        }
    }
}
