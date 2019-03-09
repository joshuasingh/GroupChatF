package com.groupchat.jasonchesney.groupchat;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSettingsActivity extends AppCompatActivity {

    public Spinner spinner;
    private long t;
    private Button updatebutton;
    private EditText updatename, updatestatus;
    private CircleImageView updateimage;

    private String currentUserID;
    private String currentImage, retrievephoneno;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, userRef;
    private StorageReference userprofileRef;
    String downloadurl;

    private static final int GALLERY_PICK = 1;

    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        userprofileRef = FirebaseStorage.getInstance().getReference().child("Group Image");

        updatebutton = (Button) findViewById(R.id.gupdatebtn);
        updatename = (EditText) findViewById(R.id.set_g_name);
        updateimage = (CircleImageView) findViewById(R.id.set_g_image);
        progressbar = (ProgressBar) findViewById(R.id.gprogressBar);
        spinner = (Spinner) findViewById(R.id.gspinner);

        ArrayAdapter<CharSequence> add = ArrayAdapter.createFromResource(GroupSettingsActivity.this,
                R.array.time, android.R.layout.simple_spinner_item);
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(add);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                if(text.equals("15 minutes")){
                    t= 15;
                }
                else if(text.equals("30 minutes")){
                    t= 30;
                }
                else if(text.equals("45 minutes")){
                    t= 45;
                }
                else if(text.equals("1 hour")){
                    t= 60;
                }
                else{
                    t=5;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(GroupSettingsActivity.this, "Nothing was selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RetreieveUserInfo() {
        rootRef.child("Groups").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status")){
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    String retrievestatus = dataSnapshot.child("status").getValue().toString();
                    //retrievephoneno = dataSnapshot.child("phone_number").getValue().toString();
                    currentImage = dataSnapshot.child("image").getValue().toString();

                    updatename.setText(retrievename);
                    updatestatus.setText(retrievestatus);
                    Picasso.get().load(currentImage).into(updateimage);
                }

                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")){
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    currentImage = dataSnapshot.child("image").getValue().toString();

                    updatename.setText(retrievename);
                    //updatestatus.setText(retrievestatus);
                    Picasso.get().load(currentImage).into(updateimage);
                }

                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    updatename.setText(retrievename);
                    if((dataSnapshot.exists()) && dataSnapshot.hasChild("status")){
                        String retrievestatus = dataSnapshot.child("status").getValue().toString();

                        updatestatus.setText(retrievestatus);
                    }
                }

                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("status")){
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    String retrievestatus = dataSnapshot.child("status").getValue().toString();

                    updatename.setText(retrievename);
                    updatestatus.setText(retrievestatus);
                }
                else{
                    Toast.makeText(GroupSettingsActivity.this, "Update you profle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
