package com.example.martin.userresturant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

public class sign_in extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView password;
    private TextView email;
    private Intent sign_up;
    private Intent main;
    private DatabaseReference mref;

    public static String name_thing_in_kind;
    public static String part_table;
    public static String table_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();


        password=findViewById(R.id.sign_in_password);
        email = findViewById(R.id.sign_in_email);
        sign_up= new Intent(this,sign_up.class);
        main= new Intent(this,kind_choose.class);


    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    @Override
    public void onStart() {
        super.onStart();


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){//&&currentUser.isEmailVerified()) {
            table_details();
            finish();
            startActivity(main);
        }


    }

    public void sign_up(View view) {
         finish();
        startActivity(sign_up);
    }

    public void sign_in(View view) {
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
            Toast.makeText(this,"should enter all data please",Toast.LENGTH_LONG).show();
        }else {
            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                              /*  // Sign in success, update UI with the signed-in user's information
                                final String uid= mAuth.getCurrentUser().currentUser.getUid().toString();
                                mref.child("unblock").child(uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) { */
                                            table_details();
                                            finish();
                                            startActivity(main);
                                      /*  }else {
                                            Toast.makeText(sign_in.this, "هذا الاكونت محظور من قبل اداره المحل ", Toast.LENGTH_LONG).show();
                                            Intent back_to_sign_in =new Intent(sign_in.this,sign_in.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                            mAuth.signOut();
                                            startActivity(back_to_sign_in);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });*/
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    void table_details(){
        String uid= mAuth.getCurrentUser().getUid().toString();
        mref=FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("table details");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        String s = dataSnapshot.getValue().toString();
                    try{
                        String[] ss = s.split("-");
                        sign_in.name_thing_in_kind = ss[0];
                        sign_in.part_table = ss[1];
                        sign_in.table_number = ss[2];
                    }catch (Exception e){
                        sign_in.name_thing_in_kind = "";
                        sign_in.table_number ="";
                        sign_in.part_table="";
                    }


                }else {
                    mref.setValue(" ");
                }
                mref.removeEventListener(this);
            }
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void forget_password(View view) {
        final EditText text = new EditText(sign_in.this);
        text.setHint("your Email ...");
        text.setGravity(Gravity.CENTER);
        AlertDialog.Builder alart = new AlertDialog.Builder(sign_in.this);
        alart.setMessage("Enter your Email and check your Email Box ");
        alart.setView(text);
        alart.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if(TextUtils.isEmpty(text.getText().toString())){
                   Toast.makeText(sign_in.this, "Enter Email to Continue"+"\n process failed ", Toast.LENGTH_SHORT).show();
               }else{
                   FirebaseAuth mAuth =FirebaseAuth.getInstance();
                   mAuth.sendPasswordResetEmail(text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       public void onComplete( Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(sign_in.this, "Check your Email Box ", Toast.LENGTH_SHORT).show();
                           }else
                               Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                       }
                   });
               }
            }
        });
        alart.show();
    }
}
