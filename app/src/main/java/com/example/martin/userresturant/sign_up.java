package com.example.martin.userresturant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class sign_up extends AppCompatActivity {

    private Intent sign_in;
    private Intent main;

    private TextView fname;
    private TextView lname;
    private TextView email;
    private TextView password;
    private TextView address;
    private TextView phone;
    private TextView verify;
    private LinearLayout verify_layout;

    private String Fname;
    private String Lname;
    private String Email;
    private String Password;
    private String Address;
    private String Phone;
    private String smsCode;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verification_callbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fname = findViewById(R.id.sign_up_fname);
        lname = findViewById(R.id.sign_up_lname);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);
        address = findViewById(R.id.sign_up_address);
        phone = findViewById(R.id.sign_up_phone);
        verify = findViewById(R.id.verify);
        verify_layout=findViewById(R.id.verify_layout);

        sign_in = new Intent(this, sign_in.class);
        main = new Intent(this, kind_choose.class);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(sign_in);
    }

    public void sign_in(View view) {
        finish();
        startActivity(sign_in);
    }

    // String message = "هذا الهاتف مستخدم من قبل";
    // boolean first_time =true;
    public void submit(View view) {
        Fname = fname.getText().toString();
        Lname = lname.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        Address = address.getText().toString();
        Phone = phone.getText().toString();

        if (TextUtils.isEmpty(Fname)) {
            Toast.makeText(this, " First name Is Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Lname)) {
            Toast.makeText(this, " Last name Is Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Email)) {
            Toast.makeText(this, " Email Is Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, " Password Is Required should be more than 6", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Address)) {
            Toast.makeText(this, " Address Is Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Phone)) {
            Toast.makeText(this, " Phone Is Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (Phone.length() != 11)
            Toast.makeText(this, "this number not valid should be 11 number", Toast.LENGTH_SHORT).show();
        else
            Verify_phone_number();
    }


    private void Verify_phone_number() {
        String phone_number ="+2"+ phone.getText().toString();
        setupverificationcallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_number, 2, TimeUnit.MINUTES, this, verification_callbacks);
    }


    void phone_notused_before() {

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
                    Toast.makeText(sign_up.this, "this phone used before try another one", Toast.LENGTH_SHORT).show();
                else{
                    mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String uid =  FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                                user User = new user(Fname, Lname, Address,Phone);
                                myRef.child("user").child(uid).child("personal info").setValue(User);
                                finish();
                                startActivity(main);
                                com.example.martin.userresturant.sign_in.name_thing_in_kind =  "";
                            }else {
                                Toast.makeText(sign_up.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                q.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

String phone_id;

    private void setupverificationcallbacks() {
        verification_callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential Credential) {

                smsCode=Credential.getSmsCode();
                phone_notused_before();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(sign_up.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String Verification_Id, PhoneAuthProvider.ForceResendingToken Token) {
                
                phone_id=Verification_Id;
                verify_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                Toast.makeText(sign_up.this, s, Toast.LENGTH_SHORT).show();
            }
        };
    }

   /* public void verify_code(View view) {
        String code = verify.getText().toString();

        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(phone_id,code);
      String codeee= credential.getSmsCode();


        if(TextUtils.isEmpty(code))
            Toast.makeText(this, "Enter Verify Code OR Use Resend Button", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, smsCode, Toast.LENGTH_SHORT).show();
            if (smsCode.equals(code))
                phone_notused_before();
            else
                Toast.makeText(this, "ERROR CODE", Toast.LENGTH_LONG).show();
        }

    }

    public void resend(View view) {
        String phone_number = "+2"+phone.getText().toString();
        setupverificationcallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_number, 2, TimeUnit.MINUTES, this, verification_callbacks,resend_token);
    }*/
}
