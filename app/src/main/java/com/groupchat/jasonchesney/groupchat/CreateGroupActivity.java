package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

    private DatabaseReference rootRef, gRef;
    private int s, j, i, k, t;
    private EditText groupText, address, city, pincode, codegen;
    private Button create;
    private String fetchname, randfetch1, randfetch, currentUserID, userProfileimage, currentUserName;
    public Spinner spinner;

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
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();

        groupText = (EditText) findViewById(R.id.groupname);
        address= (EditText) findViewById(R.id.address);
        city= (EditText) findViewById(R.id.city);
        pincode = (EditText) findViewById(R.id.pincode);
        codegen = (EditText) findViewById(R.id.gencode);
        create = (Button) findViewById(R.id.create);
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this,
                R.array.time, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                if(text.equals("15 minutes")){
                    t= 1500000;
                }
                else if(text.equals("30 minutes")){
                    t= 300000;
                }
                else if(text.equals("45 minutes")){
                    t= 2700000;
                }
                else if(text.equals("1 hour")){
                    t= 600000;
                }
                else{
                    t=16000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        currentUserName = bundle.getString("getusername");
        userProfileimage = bundle.getString("profilepic");

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
        char[] chars1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder str = new StringBuilder();
        Random random1 = new Random();
        for(j=1; j< lengthr; j++){
            char c = chars[random1.nextInt(chars1.length)];
            str.append(c);
            randfetch = str.toString();
        }

        codegen.setText(randfetch);

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
                            grptitleandimg.put("timer", t);
                            rootRef.child("Groups").child(fetchname).updateChildren(grptitleandimg);

                            rootRef.child("TotalGroupNumber").setValue(i);

                    Intent intent = new Intent(CreateGroupActivity.this, MainpageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
