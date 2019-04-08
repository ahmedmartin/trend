package com.example.martin.userresturant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class table extends AppCompatActivity {

    private View view;


    private Button go_to_waiter;
    private Button cash;
    private TextView waiter_price;
    private TextView cach_price;

    private DatabaseReference ref_food_unread;
    private DatabaseReference ref_food_read;
    private DatabaseReference ref_state;
    private ChildEventListener read_event_listener;
    private ChildEventListener unread_event_listener;


    private String uid;
    private double unread_sum;
    private double cash_sum;
    private String state = "";






    private ArrayList <String>unread_food=new ArrayList<>();
    private ArrayList <String>read_food=new ArrayList<>();
    private ArrayList <Double>unread_price =new ArrayList<>();
    private ArrayList <Double>read_price=new ArrayList<>();
    private ArrayList <Integer>unread_count=new ArrayList<>();
    private ArrayList <Integer>read_count=new ArrayList<>();

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private ListView unraed_food_list;
    private ListView read_food_list;
    private table_adapter unread_adapt;
    private table_adapter read_adapt;


    @Override
    protected void onStart() {
        super.onStart();
       // get_all_data();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        waiter_price=findViewById(R.id.waiter_price);
        cach_price=findViewById(R.id.cash_price);

        if(! TextUtils.isEmpty(sign_in.name_thing_in_kind)) {

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

            ref_food_unread = FirebaseDatabase.getInstance().getReference().child("resturants")
                    .child(sign_in.name_thing_in_kind).child("part").child(sign_in.part_table)
                    .child("table").child(sign_in.table_number).child("unread");

            ref_food_read = FirebaseDatabase.getInstance().getReference().child("resturants")
                    .child(sign_in.name_thing_in_kind).child("part").child(sign_in.part_table)
                    .child("table").child(sign_in.table_number).child("read");

            ref_state =  FirebaseDatabase.getInstance().getReference().child("resturants").child(sign_in.name_thing_in_kind)
                    .child("part").child(sign_in.part_table).child("table").child(sign_in.table_number).child("state");

            ref_state.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        state=dataSnapshot.getValue().toString();
                    ref_state.removeEventListener(this);
                }
                public void onCancelled(DatabaseError databaseError) {}
            });

  get_all_data();
        }




        unraed_food_list =findViewById(R.id.unread_food_list);
        unread_adapt = new table_adapter(table.this, unread_food, unread_price,unread_count);
        unraed_food_list.setAdapter(unread_adapt);
        registerForContextMenu(unraed_food_list);

        read_food_list =findViewById(R.id.read_food_list);
        read_adapt = new table_adapter(table.this, read_food, read_price,read_count);
        read_food_list.setAdapter(read_adapt);


        go_to_waiter=findViewById(R.id.go_to_waiter);//view.findViewById(R.id.go_to_waiter);
        go_to_waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unread_food.size()>0) {

                    if ((state.equals("جارى الطلب") || state.equals("طعام مضاف"))) {
                        ref_state.setValue("طعام مضاف");
                        Toast.makeText(table.this, "send successful", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(table.this, "sorry you closed the table", Toast.LENGTH_LONG).show();


                }else
                    Toast.makeText(table.this,"no order to send",Toast.LENGTH_LONG).show();
            }
        });

        cash=findViewById(R.id.cash);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (read_food.size()>0) {
                    ref_state.setValue("غلق الطاوله");
                    ref_food_unread.removeValue();
                    Toast.makeText(table.this, "closed successful " + "\n will send check for you now", Toast.LENGTH_LONG).show();
                    waiter_price.setText("");
                }else
                    Toast.makeText(table.this,"no order completed to close table",Toast.LENGTH_LONG).show();

            }
        });


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.unread_food_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(unread_food.get(info.position));
            String[] menuItems = {"DELETE" ,"EDIT" };
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"DELETE" ,"EDIT" };
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = unread_food.get(info.position);

        if(menuItemName.equals("DELETE"))
            delete(listItemName,info.position);
        else
            update(listItemName,info.position);

        return true;
    }

    public void get_all_data(){
        cash_sum=0;
        unread_sum=0;

        unread_event_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String ss) {
                if(dataSnapshot.exists()) {
                    unread_food.add(dataSnapshot.getKey().toString());
                    String s = dataSnapshot.getValue().toString();
                    String a[] = s.split("x");
                    unread_price.add(Double.parseDouble(a[0]));
                    unread_count.add(Integer.parseInt(a[1]));
                    unread_sum += (Double.parseDouble(a[0]) * Integer.parseInt(a[1]));
                    unread_adapt.notifyDataSetChanged();
                    waiter_price.setText("TOTAL added: " + Double.valueOf(decimalFormat.format(unread_sum)));
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String ss) {        }
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                int position = unread_food.indexOf(dataSnapshot.getKey().toString());
                unread_food.remove(position);
                unread_sum -= (unread_price.get(position) * unread_count.get(position));
                waiter_price.setText("TOTAL added: " + Double.valueOf(decimalFormat.format(unread_sum)));
                unread_price.remove(position);
                unread_count.remove(position);
                unread_adapt.notifyDataSetChanged();
                if(unread_food.size()==0)
                    waiter_price.setText("TOTAL added: " + 0);
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

        ref_food_unread.addChildEventListener(unread_event_listener);


         read_event_listener = new ChildEventListener() {
             @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String ss) {
                 read_food.add(dataSnapshot.getKey().toString());
                 String s = dataSnapshot.getValue().toString();
                 String a[] = s.split("x");
                 read_price.add(Double.parseDouble(a[0]));
                 read_count.add(Integer.parseInt(a[1]));
                 cash_sum += (Double.parseDouble(a[0]) * Integer.parseInt(a[1]));
                 cach_price.setText("TOTAL : " + Double.valueOf(decimalFormat.format(cash_sum)));
                 read_adapt.notifyDataSetChanged();
             }
             public void onChildChanged(DataSnapshot dataSnapshot, String ss) {
                 int position;
                 position = read_food.indexOf(dataSnapshot.getKey().toString());
                 cash_sum-=(read_count.get(position)*read_price.get(position));
                 String s = dataSnapshot.getValue().toString();
                 String a[] = s.split("x");
                 read_count.set(position,Integer.parseInt(a[1]));
                 cash_sum+=(read_count.get(position)*read_price.get(position));
                 cach_price.setText("TOTAL : " + Double.valueOf(decimalFormat.format(cash_sum)));
                 read_adapt.notifyDataSetChanged();
             }
             public void onChildRemoved(DataSnapshot dataSnapshot) {
                     int position;
                     position = read_food.indexOf(dataSnapshot.getKey().toString());
                     read_food.remove(dataSnapshot.getKey().toString());
                     cash_sum-=(read_price.get(position)*read_count.get(position));
                     cach_price.setText("TOTAL : " + Double.valueOf(decimalFormat.format(cash_sum)));
                     read_count.remove(position);
                     read_price.remove(position);
                     read_adapt.notifyDataSetChanged();
                     if(read_food.size()==0)
                         cach_price.setText("TOTAL : " + 0);
             }
             public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
             public void onCancelled(DatabaseError databaseError) {}
         };

        ref_food_read.addChildEventListener(read_event_listener);

    }


    public void delete(String food_name,int position){

        ref_food_unread.child(food_name).removeValue();
       // unread_count.remove(position);
       /* unread_sum -= (unread_price.get(position) * unread_count.get(position));
        unread_food.remove(position);
        unread_price.remove(position);
        unread_adapt.notifyDataSetChanged();*/
    }


    void update(final String food_name , final int position){

        View v= LayoutInflater.from(table.this).inflate(R.layout.input_show,null);
        final EditText input = v.findViewById(R.id.inputt);
        final AlertDialog.Builder alart= new AlertDialog.Builder(table.this);
        alart.setMessage(" enter food count from "+food_name);
        alart.setTitle("food count");
        alart.setView(v);
        alart.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
                int order_num=0;
                try {
                    order_num=Integer.parseInt(input.getText().toString());
                }catch (Exception e){
                    Toast.makeText(table.this,"enter signed number",Toast.LENGTH_SHORT).show();
                }
                ref_food_unread.child(food_name).setValue(unread_price.get(position)+"x"+order_num);
                unread_sum-=(unread_price.get(position)*unread_count.get(position));
                unread_count.set(position,order_num);
                unread_sum+=(unread_price.get(position)*unread_count.get(position));
                waiter_price.setText("TOTAL added: " + Double.valueOf(decimalFormat.format(unread_sum)));
                unread_adapt.notifyDataSetChanged();
                alart.setCancelable(true);
            }
        });
        alart.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* if(! TextUtils.isEmpty(sign_in.name_thing_in_kind)) {
            ref_food_unread.removeEventListener(unread_event_listener);
            ref_food_read.removeEventListener(read_event_listener);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(! TextUtils.isEmpty(sign_in.name_thing_in_kind)) {
            ref_food_unread.removeEventListener(unread_event_listener);
            ref_food_read.removeEventListener(read_event_listener);
        }
    }
}
