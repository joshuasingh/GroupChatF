package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class ConnectPageActivity extends AppCompatActivity {

    Button connect, createnew, signout;
    EditText memcode;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef, userRef, gidRef;
    String groupname, currentUserID, currentUserName, userProfileimage, groupid, randfetch, gencode, tget;
    int s, j, i;
    Intent i1;
    //NotificationCompat.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_page);


       //start background service
               i1=new Intent(this,GroupAuth.class);
        startService(i1);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        connect = (Button) findViewById(R.id.connect);
        createnew = (Button) findViewById(R.id.crejump);
        signout = (Button) findViewById(R.id.signout);
        memcode = (EditText) findViewById(R.id.memgencode);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef= FirebaseDatabase.getInstance().getReference();
        gidRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        getUserInfo();
//        notification  = new NotificationCompat.Builder(ConnectPageActivity.this);
//        notification.setAutoCancel(true);

        // This where the onclick function should generate request in Admin.
        createnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Intent intent = new Intent(ConnectPageActivity.this, CreateGroupActivity.class);
                //startActivity(intent);
                Toast.makeText(ConnectPageActivity.this," request has been given to admin",Toast.LENGTH_LONG).show();

                DatabaseReference ref11 = FirebaseDatabase.getInstance().getReference("Admin").child("Pending Request").child(currentUserID);
                ref11.setValue("first");


            }
        });

        // to signout to login page
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(i1);
                mAuth.signOut();
                Intent intent = new Intent(ConnectPageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // This code snippit randomly generates 2 numbers as the part of members id
        int lengthr = 2;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder str = new StringBuilder();
        final Random random = new Random();
        for(j=1; j< lengthr; j++){
            char c = chars[random.nextInt(chars.length)];
            str.append(c);
            randfetch = str.toString();
        }

        // the connect button after entering the 5 digit code takes user to the specified group through the Code
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!memcode.getText().toString().equals("")){
                    gencode = memcode.getText().toString();

                    rootRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("GroupId")){
                                groupname = dataSnapshot.child("GroupId").child(gencode)
                                        .getValue().toString();
                                groupid = dataSnapshot.child("GroupId").child(gencode)
                                        .getRef().getKey();
                            }

                        if(dataSnapshot.child("Groups").child(groupname).child("Members").hasChild("total_members")){
                            String v= dataSnapshot.child("Groups").child(groupname).child("Members").child("total_members").getValue().toString();
                            s = Integer.parseInt(v)+1;
                        }
                        else{
                            s=2;
                        }

                        if(dataSnapshot.child("Groups").child(groupname).child("Timer").hasChild("timer")) {
                                tget = dataSnapshot.child("Groups").child(groupname).child("Timer").child("timer").getValue().toString();
                            }

                        for(i=2; i<100; i++){
                            if(s == i){
                                break;
                            }
                        }

                            if(gencode.equals(memcode.getText().toString())) {

                                //the hash map store the data of the user into the particular group
                                HashMap<String, Object> mem = new HashMap<>();
                                mem.put("pname", currentUserName);
                                mem.put("pimage", userProfileimage);
                                mem.put("member_type", "Group Member");
                                mem.put("member_id", groupid + " " + randfetch + "0" + i);
                                rootRef.child("Groups").child(groupname).child("Members").child(currentUserID).updateChildren(mem);

                                rootRef.child("Groups").child(groupname).child("total_members")
                                        .setValue(i);

                                Intent intent = new Intent(ConnectPageActivity.this, SelectdGroupActivity.class);
                                intent.putExtra("newGroupName", groupname);
                                intent.putExtra("time", tget);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(ConnectPageActivity.this, "Code Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(ConnectPageActivity.this, "Nothing entered", Toast.LENGTH_SHORT)
                            .show();
                }

//                gidRef.child(groupname).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.hasChild("total_members")){
//                            String v= dataSnapshot.child("total_members").getValue().toString();
//                            s = Integer.parseInt(v)+1;
//                        }
//                        else{
//                            s=2;
//                        }
//
//                        int lengthr = 2;
//                        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
//                        StringBuilder str = new StringBuilder();
//                        final Random random = new Random();
//                        for(j=1; j< lengthr; j++){
//                            char c = chars[random.nextInt(chars.length)];
//                            str.append(c);
//                            randfetch = str.toString();
//                        }
//
//                        for(i=2; i<100; i++){
//                            if(s == i){
//                                break;
//                            }
//                        }
//
////                        notification.setSmallIcon(R.drawable.ticker);
////                        notification.setWhen(System.currentTimeMillis());
////                        notification.setContentTitle("Generated Code :");
////                        notification.setContentText(groupid);
//
//                        gencode = groupid;
//
////                        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
////                        nm.notify(1, notification.build());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }
        });
    }
// pre requisites gathered from the database to fetch particular users name and Pro.Pic to add inside the GROUPS database
    private void getUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                    userProfileimage = dataSnapshot.child("image").getValue().toString();
                }
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name"))){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //This Condition is used to so that once a person is logged in wont go back to the login page onStart
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            sendUsertoLogin();
        }
    }
//sending the user back to the login through intent
    private void sendUsertoLogin() {
        Intent intent= new Intent(ConnectPageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
