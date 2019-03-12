package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GivePer extends AppCompatActivity {


   private TextView t1;
   private Button b1,b2;
   private String str;
   private DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_per);


        t1=(TextView) findViewById(R.id.t1);
        b1=(Button) findViewById(R.id.b1);
        b2=(Button) findViewById(R.id.b2);


        str= getIntent().getStringExtra("user");

        t1.setText("would you give permission to "+str+"to create a group");


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db= FirebaseDatabase.getInstance().getReference().child("Users").child(str).child("grouper");
                db.setValue(1);


                Intent i1=new Intent();
               GivePer.this.finish();
            }
        });



        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1=new Intent();
                GivePer.this.finish();
            }
        });


    }
}
