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

public class ConnectPageActivity extends AppCompatActivity {

    Button connect;
    EditText memcode;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef, userRef, gidRef;
    String groupname, currentUserID, currentUserName, userProfileimage, groupid, randfetch, gencode;
    int s, j, i;
    //NotificationCompat.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_page);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        connect = (Button) findViewById(R.id.connect);
        memcode = (EditText) findViewById(R.id.memgencode);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef= FirebaseDatabase.getInstance().getReference();
        gidRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        getUserInfo();

//        notification  = new NotificationCompat.Builder(ConnectPageActivity.this);
//        notification.setAutoCancel(true);

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

//                        if(dataSnapshot.child("Groups").child(groupname).hasChild("total_members")){
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

                            if(gencode.equals(memcode.getText().toString())) {

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

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            sendUsertoLogin();
        }
    }

    private void sendUsertoLogin() {
        Intent intent= new Intent(ConnectPageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
