package com.groupchat.jasonchesney.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomChatList extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private DatabaseReference userRef;
    private String members, currentGroupName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.bottom_sheets_view, container, false);

        members = getArguments().getString("memnames");
        currentGroupName = getArguments().getString("gname");

        userRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child(members);

        recyclerView = (RecyclerView) v.findViewById(R.id.chat_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, GroupInfoViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, GroupInfoViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupInfoViewHolder holder, int position, @NonNull Contacts model) {

                        holder.username.setText(model.getName());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profileimg).into(holder.profileimage);
                    }

                    @NonNull
                    @Override
                    public GroupInfoViewHolder onCreateViewHolder(@NonNull ViewGroup vgroup, int viewType) {
                        View view = LayoutInflater.from(vgroup.getContext()).inflate(R.layout.user_display_layout,
                                vgroup, false);
                        GroupInfoViewHolder groupInfoViewHolder = new GroupInfoViewHolder(view);
                        return groupInfoViewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class GroupInfoViewHolder extends RecyclerView.ViewHolder{

        TextView username, userstatus;
        CircleImageView profileimage;

        public GroupInfoViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            profileimage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}
