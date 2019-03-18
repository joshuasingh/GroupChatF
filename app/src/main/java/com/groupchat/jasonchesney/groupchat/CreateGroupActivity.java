package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateGroupActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    private DatabaseReference rootRef, gRef, userRef;
    private int s, j, i, k;
    private EditText groupText, address, city, pincode, codegen;
    private Button create,gen;
    private String fetchname, randfetch1, randfetch, currentUserID, userProfileimage, currentUserName,
                   phonenum;
    public Spinner spinner;
    private long t;
    private String c1,c2,c3,c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
        //currentUserID = mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //aa
        groupText = (EditText) findViewById(R.id.groupname);
        //aa
        address= (EditText) findViewById(R.id.address);
        //aa
        city= (EditText) findViewById(R.id.city);
        //aa
        pincode = (EditText) findViewById(R.id.pincode);
        codegen = (EditText) findViewById(R.id.gencode);
        create = (Button) findViewById(R.id.create);
         gen= ( Button ) findViewById(R.id.newGene);
        spinner = (Spinner) findViewById(R.id.spinner);



       // getUserInfo();

        ArrayAdapter<CharSequence> adw = ArrayAdapter.createFromResource(CreateGroupActivity.this,
                R.array.time, android.R.layout.simple_spinner_item);
        adw.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adw);
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
                Toast.makeText(CreateGroupActivity.this, "Nothing was selected", Toast.LENGTH_SHORT).show();
            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("TotalGroupNumber")){
                    String v= dataSnapshot.child("TotalGroupNumber").getValue().toString();
                    s = Integer.parseInt(v)+1;
                }
                else{
                    s=1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int len = 2;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder str1 = new StringBuilder();
        Random random = new Random();
        for(k=1; k< len; k++){
            char c1 = chars[random.nextInt(chars.length)];
            str1.append(c1);
            randfetch1 = str1.toString();
        }
        int lengthr = 6;


        /*Random random1 = new Random();
        for(j=1; j< lengthr; j++){
            char c = chars[random1.nextInt(chars1.length)];
            str.append(c);
            randfetch = str.toString();
        }*/


        //generate group id
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  groupGen();
            }
        });



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchname = groupText.getText().toString();
                if(TextUtils.isEmpty(fetchname)){
                    Toast.makeText(CreateGroupActivity.this, "Please Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(i=1; i<10; i++){
                        if(s == i) {
                            break;
                        }
                    }

                            HashMap<String, Object> grp = new HashMap<>();
                            grp.put(phonenum, phonenum);
                            rootRef.child("GroupOwner").updateChildren(grp);

                            HashMap<String, Object> mem = new HashMap<>();
                            mem.put("pname", currentUserName);
                            mem.put("pimage", userProfileimage);
                            mem.put("member_type", "Group Owner");
                            mem.put("member_id", randfetch+" "+randfetch1+"01");
                            rootRef.child("Groups").child(fetchname).child("Members").child(currentUserID).updateChildren(mem);

                            HashMap<String, Object> grptitleandimg = new HashMap<>();
                            grptitleandimg.put("grouptitle", fetchname);
                            grptitleandimg.put("priority", currentUserID);
                            grptitleandimg.put("group_id", randfetch);
                            rootRef.child("Groups").child(fetchname).updateChildren(grptitleandimg);

                            HashMap<String, Object> gtime = new HashMap<>();
                            gtime.put("timer", t);
                            rootRef.child("Groups").child(fetchname).child("Timer").updateChildren(gtime);

                            HashMap<String, Object> gid = new HashMap<>();
                            gid.put(randfetch, fetchname);
                            rootRef.child("GroupId").updateChildren(gid);

                            rootRef.child("TotalGroupNumber").setValue(i);

                            String tget = String.valueOf(t).toString();

                    Intent intent = new Intent(CreateGroupActivity.this, SelectdGroupActivity.class);
                    intent.putExtra("newGroupName", fetchname);
                    intent.putExtra("timer", tget);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void groupGen()
    {
        c1=groupText.getText().toString();
        c2=address.getText().toString();
        c3=city.getText().toString();
        c4=pincode.getText().toString();
        char[] chars1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
         Random rand=new Random();

        if(!c1.equals("") && !c2.equals("") && !c3.equals("") && !c4.equals("")) {
            StringBuilder str = new StringBuilder();
            str.append(c1.charAt(0));
            str.append(c2.charAt(0));
            str.append(c3.charAt(0));
            str.append(c4.charAt(0));
            str.append(chars1[rand.nextInt(26)]);
            randfetch = str.toString();
            codegen.setText(randfetch);
        }
        else
        {
            Toast.makeText(CreateGroupActivity.this,"Enter all the neccessary entries",Toast.LENGTH_LONG).show();
        }

    }


    private void getUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                    userProfileimage = dataSnapshot.child("image").getValue().toString();
                    phonenum = dataSnapshot.child("phone_number").getValue().toString();
                }
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name"))){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                    phonenum = dataSnapshot.child("phone_number").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
