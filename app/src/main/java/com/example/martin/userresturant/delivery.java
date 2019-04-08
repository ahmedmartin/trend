package com.example.martin.userresturant;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class delivery extends AppCompatActivity {


    private TextView price;
    private EditText address;

    private ListView food_list;
    private delivery_adapter adapt;


    private DatabaseReference myref;
    private String uid;
    private double sum = 0;

    private ArrayList<String> foods = new ArrayList();
    private ArrayList<Double> pricies = new ArrayList<>();
    private ArrayList<Integer> count = new ArrayList<>();
    private ArrayList<String> resturants = new ArrayList<>();

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);



        // delivery_food_name =findViewById(R.id.delivery_food_name);//view.findViewById(R.id.delivery_food_name);
        price = findViewById(R.id.prices);//view.findViewById(R.id.price);
        address = findViewById(R.id.address);//view.findViewById(R.id.address);
        prog = findViewById(R.id.progressBar2);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        myref = FirebaseDatabase.getInstance().getReference().child("trend").child("unsend").child(uid);

        DatabaseReference ref_address = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("personal info").child("address");
        ref_address.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    address.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button deliver = findViewById(R.id.deliver);// view.findViewById(R.id.deliver);
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliver();
            }
        });

        food_list = findViewById(R.id.delivery_food_list);//view.findViewById(R.id.delivery_food_list);
        adapt = new delivery_adapter(delivery.this, foods, pricies, count, resturants);
        food_list.setAdapter(adapt);
        registerForContextMenu(food_list);


        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    i++;
                    for (DataSnapshot Snapshot : data.getChildren()) {
                        resturants.add(data.getKey().toString());
                        foods.add(Snapshot.getKey().toString());
                        String s = Snapshot.getValue().toString();
                        String a[] = s.split("x");
                        pricies.add(Double.parseDouble(a[0]));
                        count.add(Integer.parseInt(a[1]));
                        adapt.notifyDataSetChanged();
                        sum += (Double.parseDouble(a[0]) * Integer.parseInt(a[1]));
                    }
                }
                if (i == dataSnapshot.getChildrenCount()) {
                  //  Double d =Double.valueOf(decimalFormat.format(sum));
                    String s ="TOTAL : " +sum;
                    price.setText(s);
                }

                myref.removeEventListener(this);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.delivery_food_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(foods.get(info.position));
            String[] menuItems = {"DELETE", "EDIT"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"DELETE", "EDIT"};
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = foods.get(info.position);

        if (menuItemName.equals("DELETE"))
            delete(resturants.get(info.position), listItemName, info.position);
        else
            update(resturants.get(info.position), listItemName, info.position);

        return true;
    }

    public void delete(String resturant, String food_name, int position) {

        myref.child(resturant).child(food_name).removeValue();
        sum -= (pricies.get(position) * count.get(position));
        foods.remove(position);
        pricies.remove(position);
        count.remove(position);
        resturants.remove(position);
        adapt.notifyDataSetChanged();
        price.setText("TOTAL : " + Double.valueOf(decimalFormat.format(sum)));
    }


    void update(final String resturant, final String food_name, final int position) {

        View v = LayoutInflater.from(delivery.this).inflate(R.layout.input_show, null);
        final EditText input = v.findViewById(R.id.inputt);
        final AlertDialog.Builder alart = new AlertDialog.Builder(delivery.this);
        alart.setMessage(" enter food count from " + food_name);
        alart.setTitle("food count");
        alart.setView(v);
        alart.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
                int order_num = 0;
                try {
                    order_num = Integer.parseInt(input.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(delivery.this, "enter signed number", Toast.LENGTH_SHORT).show();
                }
                myref.child(resturant).child(food_name).setValue(pricies.get(position) + "x" + order_num);
                sum -= (pricies.get(position) * count.get(position));
                count.set(position, order_num);
                sum += (pricies.get(position) * count.get(position));
                adapt.notifyDataSetChanged();
                price.setText("TOTAL : " + Double.valueOf(decimalFormat.format(sum)));
            }
        });
        alart.show();
    }


    public void deliver() {

        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("trend");
        for (int i = 0; i < resturants.size(); i++) {
            dr.child("read").child(resturants.get(i)).child(uid).child(foods.get(i)).setValue(pricies.get(i) + "x" + count.get(i));
            dr.child("read").child(resturants.get(i)).child(uid).child("address").setValue(address.getText().toString());
        }
        resturants.clear();
        foods.clear();
        pricies.clear();
        count.clear();
        adapt.notifyDataSetChanged();
        myref.removeValue();
    }

    private LocationManager lm;
    private LocationListener location_listen;
    ProgressBar prog;
    public void my_location(View view) {


       /* if(! lm.isProviderEnabled(lm.GPS_PROVIDER)){
            Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(it);
            Toast.makeText(delivery.this, "must open the location", Toast.LENGTH_LONG).show();
        }*/

        Toast.makeText(this, "Close Up Street", Toast.LENGTH_LONG).show();

       prog.setVisibility(View.VISIBLE);

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        location_listen = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geo = new Geocoder(delivery.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {

                }
                if (list.size() > 0)
                    address.setText(list.get(0).getAddressLine(0));

                lm.removeUpdates(location_listen);
                prog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(delivery.this, " the location is opened ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(it);
                Toast.makeText(delivery.this, "must open the location ", Toast.LENGTH_LONG).show();
                prog.setVisibility(View.INVISIBLE);
            }
        };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }else {
                Location location =lm.getLastKnownLocation(lm.GPS_PROVIDER);
                if(location==null)
                    location=lm.getLastKnownLocation(lm.PASSIVE_PROVIDER);
                if(location!=null) {
                    Geocoder geo = new Geocoder(delivery.this);
                    List<Address> list = new ArrayList<>();
                    try {
                        list = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {

                    }
                    if (list.size() > 0)
                        address.setText(list.get(0).getAddressLine(0));
                }
                lm.requestLocationUpdates(lm.GPS_PROVIDER, 1000, 1000, location_listen);

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 10) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(lm.GPS_PROVIDER, 1000, 1000, location_listen);
            }else{
                Toast.makeText(delivery.this, "must get us location permission to can get location", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prog.setVisibility(View.INVISIBLE);
        if(lm!=null||location_listen!=null)
        lm.removeUpdates(location_listen);

        lm=null;
        location_listen=null;
    }
}
