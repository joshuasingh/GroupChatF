package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class GroupOwnerActivity extends AppCompatActivity {

    Button login, connect, dhaa, admin;
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
        setContentView(R.layout.activity_group_owner);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        login = (Button) findViewById(R.id.glogin);
        lphone = (AutoCompleteTextView) findViewById(R.id.phonegauth);
        progressbar = (ProgressBar) findViewById(R.id.gprogressBar);

        mAuth = FirebaseAuth.getInstance();
        registerRef = FirebaseDatabase.getInstance().getReference().child("Registered");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        userprofileRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupownerLogin();
            }
        });
    }

    public void onStart() {

        super.onStart();
        if (currentUser != null) {
            Intent intent = new Intent(GroupOwnerActivity.this, ConnectPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void groupownerLogin() {

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

        rootRef.child("GroupOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                if (!dataSnapshot.hasChild(phonenumber)) {
                    Toast.makeText(GroupOwnerActivity.this, "Your are not a Group Owner", Toast.LENGTH_LONG).show();
                }
                else{
                        Intent intent = new Intent(GroupOwnerActivity.this, VerifyOwnerActivity.class);
                        intent.putExtra("adn", phonenumber);
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    Toast.makeText(GroupOwnerActivity.this, "nw", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
