package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar tb;
    private ScrollView sc;
    private EditText edt;
    private Button sendbtn;
    private TextView displaychat, mTextViewCTD;
    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime,
                   userProfileimage, x;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, gnameRef, gmsgkeyRef, memberref, gRef;
    private String message, messageKey;

    CountDownTimer cdt;
    private static final long START_TIME_IN_MILLIS = 16000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Bundle bundle = getIntent().getExtras();
        currentGroupName = bundle.getString("gnam");

        mTextViewCTD= (TextView) findViewById(R.id.text_view_countdown);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        gnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        memberref = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members");

        tb = (Toolbar) findViewById(R.id.groupchattoolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(currentGroupName);

        sendbtn = (Button) findViewById(R.id.sendbtn);
        edt = (EditText) findViewById(R.id.chattxt);
        displaychat = (TextView) findViewById(R.id.gchattxt);
        sc = (ScrollView) findViewById(R.id.myscrollview);

        Bundle b= getIntent().getExtras();
        if(b != null) {
            x = b.getString("Vvalue");
            if(x.equals("a")){
                mTextViewCTD.setVisibility(View.VISIBLE);
                startTimer();
            }
        }
        else{
            b = savedInstanceState;
        }

        getUserInfo();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMsgToDatabase();
                edt.setText("");
            }
        });
    }

    private void startTimer(){
        cdt = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updatecdt();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                startActivity(new Intent(GroupChatActivity.this, MainpageActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }.start();
        mTimerRunning = true;
    }

    private void updatecdt(){
        int minutes= (int) (mTimeLeftInMillis/1000)/60;
        int seconds= (int) (mTimeLeftInMillis/1000)%60;

        String timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCTD.setText(timeLeftFormated);
    }

    @Override
    protected void onStart() {
        super.onStart();

        gnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("date"))){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("date"))){
                    DisplayMessages(dataSnapshot);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.chat_list_menu, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()  == R.id.ginfo){
            openchatlist();
        }
        return true;
    }

    private void openchatlist() {
        Bundle bundle = new Bundle();
        bundle.putString("memnames", "Members");
        bundle.putString("gname", currentGroupName);
        // set Fragmentclass Arguments
        BottomChatList chatsheet = new BottomChatList();
        chatsheet.setArguments(bundle);
        chatsheet.show(getSupportFragmentManager(),"listofallgroupmembers");

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

    private void saveMsgToDatabase() {
          message = edt.getText().toString();
          messageKey = gnameRef.push().getKey();


        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calDate.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            gnameRef.updateChildren(groupMessageKey);
            gmsgkeyRef = gnameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("userid", currentUserID);
            gmsgkeyRef.updateChildren(messageInfoMap);


            HashMap<String, Object> idKey = new HashMap<>();
            memberref.updateChildren(idKey);
            gRef = memberref.child(currentUserID);

            HashMap<String, Object> mem = new HashMap<>();
            mem.put("name", currentUserName);
            mem.put("image", userProfileimage);
            gRef.updateChildren(mem);
        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        if (dataSnapshot.exists()) {
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMsg  = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();
            String userid   = (String) ((DataSnapshot) iterator.next()).getValue();

            displaychat.append(chatName + " :\n" + chatMsg + "\n" + chatTime + "     " + chatDate + "\n\n\n");
        }
    }
}
