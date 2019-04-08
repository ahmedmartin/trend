package com.example.martin.userresturant;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class person_info extends AppCompatActivity {

    private EditText fname;
    private EditText lname;
    private TextView email;
    private EditText address;
    private EditText phone;

    private String Fname;
    private String Lname;
    private String Email;
    private String Address;
    private String Phone;
    private String uid;

    private user User;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        fname=findViewById(R.id.update_fname);
        lname=findViewById(R.id.update_lname);
        email=findViewById(R.id.update_email);
        address=findViewById(R.id.update_address);
        phone=findViewById(R.id.update_phone);

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        myRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("personal info");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User = dataSnapshot.getValue(user.class);
                    fname.setText(User.getFname());
                    lname.setText(User.getLname());
                    address.setText(User.getAddress());
                    phone.setText(User.getPhone());
                    Phone =User.getPhone();
                    email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                }
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void update(View view) {
        entry_fill();
    }

    private void entry_fill() {

        if (TextUtils.isEmpty(fname.getText().toString())) {
            Toast.makeText(this, " First name Is Requered", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lname.getText().toString())) {
            Toast.makeText(this, " Last name Is Requered", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(address.getText().toString())) {
            Toast.makeText(this, " Address Is Requered", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(this, " Phone Is Requered", Toast.LENGTH_SHORT).show();
            return;
        }else if (!phone.getText().toString().equals(Phone)) {
            if (phone.getText().toString().length() != 11)
                Toast.makeText(this, "this number not valid should be 11 number", Toast.LENGTH_SHORT).show();
            else
                phone_notused_before();
        }else {
            setdata();
            user User = new user(Fname, Lname, Address,Phone);
            myRef.setValue(User);
            Toast.makeText(person_info.this, "Changed Successfully", Toast.LENGTH_SHORT).show();
        }
    }


    void phone_notused_before(){

        final DatabaseReference q=FirebaseDatabase.getInstance().getReference().child("user");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                  boolean have_same_number = false;
                  for(DataSnapshot data : dataSnapshot.getChildren()){
                      if(data.child("personal info").child("phone").getValue().toString().equals(phone.getText().toString())){
                          have_same_number =true;
                          break;
                      }
                  }

                  if(have_same_number)
                      Toast.makeText(person_info.this, "this phone used before try another one", Toast.LENGTH_SHORT).show();
                  else{
                      setdata();
                      user User = new user(Fname, Lname, Address,Phone);
                      myRef.setValue(User);
                      Toast.makeText(person_info.this, "Changed Successfully", Toast.LENGTH_SHORT).show();
                  }
                  q.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    void setdata(){
        Fname=fname.getText().toString();
        Lname=lname.getText().toString();
        Address=address.getText().toString();
        Phone =phone.getText().toString();
    }


    public void change_password(View view) {

      final EditText text = new EditText(person_info.this);
      text.setHint("new Password ..");
      text.setGravity(Gravity.CENTER);
      text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder alart = new AlertDialog.Builder(person_info.this);
        alart.setMessage("Enter your new Password");
        alart.setView(text);
        alart.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(text.getText().toString())){
                    Toast.makeText(person_info.this, "no thing happend ", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(person_info.this, "your Password changed Successfully", Toast.LENGTH_LONG).show();
                                FirebaseAuth mauth=FirebaseAuth.getInstance();
                                mauth.signOut();
                                Intent sign_in=new Intent(person_info.this,sign_in.class);
                                sign_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(sign_in);
                            }else
                                Toast.makeText(person_info.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        alart.show();

    }
}
