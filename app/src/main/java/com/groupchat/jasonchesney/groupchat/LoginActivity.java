package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    Button login, connect, dhaa;
    AutoCompleteTextView lphone;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private StorageReference userprofileRef;
    private DatabaseReference registerRef, userRef, rootRef;
    ProgressBar progressbar;
    String phonecheck, phonenumber, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        login = (Button) findViewById(R.id.login);
        dhaa = (Button) findViewById(R.id.dhaa);
        lphone = (AutoCompleteTextView) findViewById(R.id.phoneauth);
        progressbar = (ProgressBar) findViewById(R.id.lprogressBar);

        mAuth = FirebaseAuth.getInstance();
        registerRef = FirebaseDatabase.getInstance().getReference().child("Registered");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        userprofileRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        dhaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    public void onStart() {

        super.onStart();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, ConnectPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void userLogin() {

        String phoneno = lphone.getText().toString().trim();
        phonenumber = "+91" + phoneno;

        if (phoneno.isEmpty()) {
            lphone.setError("Phone number is required");
            lphone.requestFocus();
            return;
        }

        if (!Patterns.PHONE.matcher(phoneno).matches()) {
            lphone.setError("Please enter a valid phone number");
            lphone.requestFocus();
            return;
        }

        userRef.child(phonenumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            rootRef.child("Registered").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(phonenumber)) {
                        Toast.makeText(LoginActivity.this, "Number is not registered", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("phonenumber", phonenumber);
                        intent.putExtra("name", name);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
