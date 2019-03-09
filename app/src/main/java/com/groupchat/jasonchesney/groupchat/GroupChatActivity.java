package com.groupchat.jasonchesney.groupchat;

import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private EditText edt;
    private Button sendbtn;
    private TextView timer, mTextViewCTD;
    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime,
                   userProfileimage;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, gnameRef, gmsgkeyRef, memberref, gRef;
    private String message, messageKey;

    CountDownTimer cdt;
    private static final long START_TIME_IN_MILLIS = 16000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;

    private final List<InstantMessage> chatList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatListAdapter mAdapter;
    private RecyclerView msgrecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        //mTextViewCTD= (TextView) findViewById(R.id.text_view_countdown);
        //mTextViewCTD.setVisibility(View.VISIBLE);
        //timer = (TextView) findViewById(R.id.text_view_timer);
        //timer.setVisibility(View.VISIBLE);

        sendbtn = (Button) findViewById(R.id.sendbtn);
        edt = (EditText) findViewById(R.id.chattxt);

        currentGroupName = getIntent().getStringExtra("groupName");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        gnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        memberref = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");

        mAdapter = new ChatListAdapter(chatList);
        msgrecycler = (RecyclerView) findViewById(R.id.chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        msgrecycler.setLayoutManager(linearLayoutManager);
        msgrecycler.setAdapter(mAdapter);

        getUserInfo();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMsgToDatabase();
                edt.setText("");
            }
        });
    }

    private void getUserInfo() {
            userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                        currentUserName = dataSnapshot.child("name").getValue().toString();
                        userProfileimage = dataSnapshot.child("image").getValue().toString();
                    }
                    if (dataSnapshot.exists() && (dataSnapshot.hasChild("name"))) {
                        currentUserName = dataSnapshot.child("name").getValue().toString();
                        String y = currentUserName;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    private void saveMsgToDatabase() {
        message = edt.getText().toString();
        messageKey = gnameRef.child("Messages").push().getKey();


        if(TextUtils.isEmpty(message)){
            edt.setText("");
        }
        else {
//            Calendar calDate = Calendar.getInstance();
//            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
//            currentDate = currentDateFormat.format(calDate.getTime());
//
//            Calendar calTime = Calendar.getInstance();
//            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
//            currentTime = currentTimeFormat.format(calTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            gnameRef.updateChildren(groupMessageKey);
            gmsgkeyRef = gnameRef.child("Messages").child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("cname", currentUserName);
            messageInfoMap.put("message", message);
            //messageInfoMap.put("date", currentDate);
            //messageInfoMap.put("time", currentTime);
            messageInfoMap.put("Userid", currentUserID);
            gmsgkeyRef.updateChildren(messageInfoMap);

            HashMap<String, Object> idKey = new HashMap<>();
            memberref.updateChildren(idKey);
            gRef = memberref.child(currentUserID);

            HashMap<String, Object> mem = new HashMap<>();
            mem.put("pname", currentUserName);
            mem.put("image", userProfileimage);
            gRef.updateChildren(mem);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
        gnameRef.child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                InstantMessage instantMessage = dataSnapshot.getValue(InstantMessage.class);
                chatList.add(instantMessage);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    catch(Exception e) {}
    }
}
