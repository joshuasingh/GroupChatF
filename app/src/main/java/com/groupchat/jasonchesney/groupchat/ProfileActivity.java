package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_profile);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userprofileRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        updatebutton = (Button) findViewById(R.id.updatebtn);
        updatename = (EditText) findViewById(R.id.set_user_name);
        updatestatus = (EditText) findViewById(R.id.set_status);
        updateimage = (CircleImageView) findViewById(R.id.set_profile_image);
        progressbar = (ProgressBar) findViewById(R.id.pprogressBar);

        RetreieveUserInfo();

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile();
            }
        });

        updateimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_PICK);
            }
        });
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrievephoneno = dataSnapshot.child("phone_number").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){
            Uri imageuri = data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                progressbar.setVisibility(View.VISIBLE);
                Uri resulturi = result.getUri();
                StorageReference filepath = userprofileRef.child(currentUserID + ".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                             downloadurl = task.getResult().getDownloadUrl()
                                    .toString();
                            rootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadurl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "Image Saved Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                progressbar.setVisibility(View.GONE);
                                            }
                                            else {
                                                String msg = task.getException().toString();
                                                Toast.makeText(ProfileActivity.this, "Error: " + msg,
                                                        Toast.LENGTH_SHORT).show();
                                                progressbar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(ProfileActivity.this, "Error: " + message,
                                    Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    */

    private void RetreieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
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
                    //String retrievestatus = dataSnapshot.child("status").getValue().toString();
                    //retrievephoneno = dataSnapshot.child("phone_number").getValue().toString();
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
                    Toast.makeText(ProfileActivity.this, "Update you profle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void UpdateProfile() {
        String setUserName = updatename.getText().toString();
        String setUserStatus = updatestatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            updatename.setError("Name is required");
            updatename.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(setUserStatus)){
            updatestatus.setError("Status is required");
            updatestatus.requestFocus();
            return;
        }

        else{
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("Userid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            profileMap.put("image", currentImage);
            profileMap.put("phone_number", retrievephoneno);

            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUsertoMainPageActivity();
                                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUsertoMainPageActivity() {
        Intent updateintent= new Intent(ProfileActivity.this, MainpageActivity.class);
        updateintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(updateintent);
        finish();
    }
}
