package com.groupchat.jasonchesney.groupchat;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupAuth extends IntentService {

   DataSnapshot ds;
   String currentUserID;
   private static final String TAG="com.groupchat.jasonchesney.groupchat";

    public GroupAuth(String name) {
        super(name);
    }

    public GroupAuth() {
        super("sfds");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        super.onDestroy();
       Log.i(TAG,"in destroy");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {

        //check datasnapshot
        DatabaseReference ref45 = FirebaseDatabase.getInstance().getReference().child("Users");

        ref45.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                ds = dataSnapshot;
                //check fr group permission
                checkUpdate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();






    }




    @SuppressLint("LongLogTag")
    public void checkUpdate()
    {
        DatabaseReference ref45 = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("grouper");
        String name="";
          int val=0;
        for (DataSnapshot datasnap : ds.child(currentUserID).getChildren()) {

            name = datasnap.getKey().toString();
            if(name.equals("grouper"))
            {
                val=Integer.parseInt(datasnap.getValue().toString());
            }
        }




        //Log.i(TAG,val);
         if(val==1)
         {
             ref45.setValue(0);
           Log.i(TAG,"in this looop");
             Intent dialogIntent = new Intent(this, CreateGroupActivity.class);
             dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(dialogIntent);

         }



    }

}
