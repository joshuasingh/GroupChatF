package com.groupchat.jasonchesney.groupchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class MainpageActivity extends AppCompatActivity implements ChatsFragment.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener {

    String x;
    Toolbar mtoolbar;
    AppBarLayout apb;
    ViewPager vp;
    TabLayout tb;
    TabsAccessorAdapter mTaa;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private DatabaseReference rootRef, memberref, gRef, userRef;

    private String currentUserID, userProfileimage, currentUserName, newGroupName;

    CountDownTimer cdt;
    private static final long START_TIME_IN_MILLIS = 6000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private TextView mTextViewCTD;
    private boolean mTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef= FirebaseDatabase.getInstance().getReference();
        //memberref = FirebaseDatabase.getInstance().getReference().child(newGroupName).child("Members");

        apb= (AppBarLayout) findViewById(R.id.appBarLayout);

        mtoolbar= (Toolbar) findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mtoolbar);

        mTextViewCTD= (TextView) findViewById(R.id.text_view_countdown);

        tb= (TabLayout) findViewById(R.id.main_tabs);
        //tb.addTab(tb.newTab().setText("Chats"));
        tb.addTab(tb.newTab().setText("Groups"));
        tb.setTabGravity(tb.GRAVITY_FILL);
        //tb.setupWithViewPager(vp);

        vp= (ViewPager) findViewById(R.id.main_tabs_pager);
        mTaa= new TabsAccessorAdapter(getSupportFragmentManager(), tb.getTabCount());
        vp.setAdapter(mTaa);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb));

        tb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            sendUsertoLogin();
        }
    }


    private void sendUsertoLogin(){
        Intent intent= new Intent(MainpageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void updatecdt(){
        int minutes= (int) (mTimeLeftInMillis/1000)/60;
        int seconds= (int) (mTimeLeftInMillis/1000)%60;

        String timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCTD.setText(timeLeftFormated);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.proupdate){
            sendUsertoProfile();
        }
        if(item.getItemId() == R.id.logout){
            mAuth.signOut();
            sendUsertoLogin();
        }
        if(item.getItemId() == R.id.group_create){
            requestNewGroup();
        }
        return  true;
    }

    private void sendUsertoProfile() {
        Intent updateintent= new Intent(MainpageActivity.this, ProfileActivity.class);
        startActivity(updateintent);
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainpageActivity.this, R.style.AlertDialog);
        builder.setTitle("Create New Group :");

        final EditText groupText= new EditText(MainpageActivity.this);
        groupText.setHint("Enter Group Name");
        builder.setView(groupText);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 newGroupName= groupText.getText().toString();
                if(TextUtils.isEmpty(newGroupName)){
                    Toast.makeText(MainpageActivity.this, "Please Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    createNewGroup();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

    private void createNewGroup() {
        rootRef.child("Groups").child(newGroupName).child("Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> idKey = new HashMap<>();
                rootRef.updateChildren(idKey);
                gRef = rootRef.child("Groups").child(newGroupName).child("Members").child(currentUserID);

                HashMap<String, Object> mem = new HashMap<>();
                mem.put("name", currentUserName);
                mem.put("image", userProfileimage);
                mem.put("member_type", "Admin");
                gRef.updateChildren(mem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
