package com.groupchat.jasonchesney.groupchat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofgroups = new ArrayList<>();
    private String currentUserID, membertype, currentGroupName, getgrp, fetch, randfetch;
    private DatabaseReference groupRef, memberRef, userRef, rootRef;
    private int i, s, j;
    private EditText otp;
    NotificationCompat.Builder notification;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        initializeFields();
        retrieveAndDisplayGroups();

        notification  = new NotificationCompat.Builder(getContext());
        notification.setAutoCancel(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                currentGroupName = adapterView.getItemAtPosition(position).toString();
                memberRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Members").child(currentUserID);
                /*memberRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            membertype = dataSnapshot.child("member_type").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(membertype.equals("Admin")) {
                    Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                    groupChatIntent.putExtra("groupName", currentGroupName);
                    startActivity(groupChatIntent);
                }
                else {
                    Intent groupChatIntent = new Intent(getContext(), VerifyActivity.class);
                    groupChatIntent.putExtra("groupName", currentGroupName);
                    startActivity(groupChatIntent);
                }*/
                groupRef.child(currentGroupName).child("Members").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("total_members")){
                            String v= dataSnapshot.child("total_members").getValue().toString();
                            s = Integer.parseInt(v)+1;
                        }
                        else{
                            s=2;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                groupRef.child(currentGroupName).child("Members").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("group_id")){
                            fetch = dataSnapshot.child("group_id").getValue().toString();
                            getgrp= fetch;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
                builder.setTitle("Connect to the group?");

                builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int lengthr = 2;
                        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                        StringBuilder str = new StringBuilder();
                        final Random random = new Random();
                        for(j=1; j< lengthr; j++){
                            char c = chars[random.nextInt(chars.length)];
                            str.append(c);
                            randfetch = str.toString();
                        }

                        for(i=2; i<100; i++){
                            if(s == i){
                                break;
                            }
                        }

                        dialog.dismiss();

                        notification.setSmallIcon(R.drawable.ticker);
                        notification.setWhen(System.currentTimeMillis());
                        notification.setContentTitle("Your One Time Passcode is :");
                        notification.setContentText(getgrp+" "+randfetch+"0"+i);

                        NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(1, notification.build());

                        AlertDialog.Builder conbuild = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
                        conbuild.setTitle("Enter One Time Passcode :");

                        otp= new EditText(getContext());
                        otp.setHint("OTP");
                        conbuild.setView(otp);

                        conbuild.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String feth = (getgrp+" "+randfetch+"0"+i).toString();
                                String otpget = otp.getText().toString();
                                if(otpget.equals(feth)) {
                                    HashMap<String, Object> memidMap = new HashMap<>();
                                    memidMap.put("member_id", otpget);
                                    memidMap.put("member_type", "Group Member");
                                    groupRef.child(currentGroupName).child("Members").child(currentUserID).updateChildren(memidMap);


                                    groupRef.child(currentGroupName).child("Members").child("total_members")
                                            .setValue(i);

                                    Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                                    groupChatIntent.putExtra("groupName", currentGroupName);
                                    startActivity(groupChatIntent);
                                }
                                else{
                                    Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });

                        conbuild.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        conbuild.show();
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
        });
        return groupFragmentView;
    }

    private void initializeFields() {
        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listofgroups);
        listView.setAdapter(arrayAdapter);
    }

    private void retrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                listofgroups.clear();
                listofgroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
