package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    String a= "a";
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId, firstname, name, phonesignup, phonenumber, memid, namefetch, numfetch, fname;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, gRef;
    Button confirm;
    EditText pveri1, pveri2, pveri3, pveri4, pveri5, pveri6;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        confirm= (Button) findViewById(R.id.confirm);
        pveri1= (EditText) findViewById(R.id.gotp1);
        pveri2= (EditText) findViewById(R.id.gotp2);
        pveri3= (EditText) findViewById(R.id.gotp3);
        pveri4= (EditText) findViewById(R.id.gotp4);
        pveri5= (EditText) findViewById(R.id.gotp5);
        pveri6= (EditText) findViewById(R.id.gotp6);

        mAuth = FirebaseAuth.getInstance();
        //currentUserID= mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        phonenumber = getIntent().getStringExtra("phonenumber");
        Bundle bundle = getIntent().getExtras();
        phonesignup = bundle.getString("phonenum");
        firstname = bundle.getString("fname");
        memid = bundle.getString("memberid");
        if(phonenumber == null) {
            sendVerificationCode(phonesignup);
        }
        else{
            sendVerificationCode(phonenumber);
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pveri1.getText().toString().trim() + pveri2.getText().toString().trim()
                        + pveri3.getText().toString().trim() + pveri4.getText().toString().trim() +
                        pveri5.getText().toString().trim() + pveri6.getText().toString().trim();

                if ((code.isEmpty() || code.length() < 6)){

                    Toast.makeText(VerifyActivity.this, "Enter the code", Toast.LENGTH_LONG).show();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        pveri1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(pveri1.getText().toString().length()== 1)     //size as per your requirement
                {
                    pveri2.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        pveri2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(pveri2.getText().toString().length()== 1)     //size as per your requirement
                {
                    pveri3.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        pveri3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(pveri3.getText().toString().length()== 1)     //size as per your requirement
                {
                    pveri4.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        pveri4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(pveri4.getText().toString().length()== 1)     //size as per your requirement
                {
                    pveri5.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        pveri5.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(pveri5.getText().toString().length()== 1)     //size as per your requirement
                {
                    pveri6.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");
                            if(phonenumber == null) {
                                numfetch = phonesignup;
                            }
                            else{
                                numfetch = phonenumber;
                            }

                            if(firstname == null) {
                                HashMap<String, Object> phoneandnameMap = new HashMap<>();
                                phoneandnameMap.put("phone_number", numfetch);
                                phoneandnameMap.put("Userid", mAuth.getCurrentUser().getUid());
                                rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(phoneandnameMap);

                                HashMap<String, Object> registermap = new HashMap<>();
                                registermap.put(numfetch, numfetch);
                                rootRef.child("Registered").updateChildren(registermap);
                            }
                            else{
                                fname = firstname;
                                HashMap<String, Object> phoneandnameMap = new HashMap<>();
                                phoneandnameMap.put("phone_number", numfetch);
                                phoneandnameMap.put("name", fname);
                                phoneandnameMap.put("Userid", mAuth.getCurrentUser().getUid());
                                rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(phoneandnameMap);

                                HashMap<String, Object> registermap = new HashMap<>();
                                registermap.put(numfetch, numfetch);
                                rootRef.child("Registered").updateChildren(registermap);
                            }

                                Intent intent = new Intent(VerifyActivity.this, ConnectPageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(VerifyActivity.this, "Verification Done", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // Log.d(TAG, "onVerificationCompleted:" + credential);
            mVerificationInProgress = false;
            Toast.makeText(VerifyActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
            signInWithCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // Log.w(TAG, "onVerificationFailed", e);
            Toast.makeText(VerifyActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(VerifyActivity.this,"Invalid Phone Number",Toast.LENGTH_SHORT).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
            }

        }

        @Override
        public void onCodeSent(String verificationId,
                PhoneAuthProvider.ForceResendingToken token) {
            // Log.d(TAG, "onCodeSent:" + verificationId);
            Toast.makeText(VerifyActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };
}
