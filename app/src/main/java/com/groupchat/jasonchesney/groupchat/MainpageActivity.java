package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainpageActivity extends AppCompatActivity {

    String x;
    Toolbar mtoolbar;
    AppBarLayout apb;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private DatabaseReference rootRef, memberref, gRef, userRef, gnameRef;

    private String currentUserID, userProfileimage, currentUserName, newGroupName, randfetch, randfetch1, memtype;
    private String MEMBER_TYPE = "Group Owner";
    EditText groupText;

    CountDownTimer cdt;
    private static final long START_TIME_IN_MILLIS = 6000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private TextView mTextViewCTD;
    private boolean mTimerRunning;
    private int i, s, j, k;

    private String fetch, getgrp;
    private Button del;
    private RecyclerView remainlist;
    private MainPageRecyclerAdapter firebasereycleradapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        gnameRef = FirebaseDatabase.getInstance().getReference("Groups");
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef= FirebaseDatabase.getInstance().getReference();

        del = (Button) findViewById(R.id.deletebtn);

        setUpRecyclerView();

        apb= (AppBarLayout) findViewById(R.id.appBarLayout);

        mtoolbar= (Toolbar) findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#254F6E'><h6>Groups</h6></font>"));

        getUserInfo();
    }

    private void setUpRecyclerView() {

        FirebaseRecyclerOptions<GroupModel> goptions = new FirebaseRecyclerOptions.Builder<GroupModel>()
                .setQuery(gnameRef, GroupModel.class).build();

        firebasereycleradapter = new MainPageRecyclerAdapter(goptions);

        remainlist = (RecyclerView) findViewById(R.id.remainlist);
        remainlist.setHasFixedSize(true);
        remainlist.setLayoutManager(new LinearLayoutManager(this));
        remainlist.setAdapter(firebasereycleradapter);

        firebasereycleradapter.setItemDeleteListener(new MainPageRecyclerAdapter.deleteItem() {
            @Override
            public void onItemDelete(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                firebasereycleradapter.notifyDataSetChanged();
            }
        });

        firebasereycleradapter.setOnItemClickListener(new MainPageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                GroupModel groupModel = dataSnapshot.getValue(GroupModel.class);
                newGroupName = dataSnapshot.getRef().getKey();

                rootRef.child("Groups").child(newGroupName).child("Members").child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("member_type")) {
                            memtype = dataSnapshot.child("member_type").getValue().toString();

                            if(memtype.equals(MEMBER_TYPE)){
                                Intent intent = new Intent(MainpageActivity.this, SelectdGroupActivity.class);
                                intent.putExtra("newGroupName", newGroupName);
                                intent.putExtra("groupid", fetch);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(MainpageActivity.this, ConnectPageActivity.class);
                                intent.putExtra("newGroupName", newGroupName);
                                intent.putExtra("groupid", fetch);
                                startActivity(intent);
                            }
                        }
                        else{
                            Intent intent = new Intent(MainpageActivity.this, ConnectPageActivity.class);
                            intent.putExtra("newGroupName", newGroupName);
                            intent.putExtra("groupid", fetch);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebasereycleradapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebasereycleradapter.stopListening();
    }

    private void sendUsertoLogin() {
        Intent intent= new Intent(MainpageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private void requestNewGroup() {
        Intent intent = new Intent(MainpageActivity.this, CreateGroupActivity.class);
        intent.putExtra("getusername", currentUserName);
        intent.putExtra("profilepic", userProfileimage);
        startActivity(intent);
    }

    private void sendUsertoProfile() {
        Intent updateintent= new Intent(MainpageActivity.this, ProfileActivity.class);
        startActivity(updateintent);
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
}
