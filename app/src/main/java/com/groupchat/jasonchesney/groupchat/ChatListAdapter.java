package com.groupchat.jasonchesney.groupchat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<InstantMessage> userMessageList;
    private DatabaseReference gnameRef;
    private FirebaseAuth mAuth;
    private String gn, groupname;

    public ChatListAdapter (List<InstantMessage> userMessageList){
        this.userMessageList = userMessageList;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView cname, cmessage, sname, smessage;

        public ChatViewHolder(View itemView) {
            super(itemView);

            cname = (TextView) itemView.findViewById(R.id.chatname);
            cmessage = (TextView) itemView.findViewById(R.id.chatmessage);
            sname = (TextView) itemView.findViewById(R.id.chatnamesender);
            smessage = (TextView) itemView.findViewById(R.id.chatmsgsender);


        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chat_msg_row, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        String currentUserID = mAuth.getCurrentUser().getUid();
        InstantMessage instantMessage = userMessageList.get(position);

        String name = instantMessage.getCname();
        String message = instantMessage.getMessage();
        String userid = instantMessage.getUserid();
        groupname = userid;

        holder.cname.setVisibility(View.INVISIBLE);
        holder.cmessage.setVisibility(View.INVISIBLE);

        if(userid.equals(currentUserID)){
            holder.smessage.setBackgroundResource(R.drawable.bubble2);
            holder.smessage.setTextColor(Color.BLACK);
            holder.smessage.setText(instantMessage.getMessage().trim());
            holder.sname.setText(instantMessage.getCname());
        }
        else{
            holder.cname.setVisibility(View.VISIBLE);
            holder.cmessage.setVisibility(View.VISIBLE);
            holder.sname.setVisibility(View.INVISIBLE);
            holder.smessage.setVisibility(View.INVISIBLE);

            holder.cmessage.setBackgroundResource(R.drawable.bubble1);
            holder.cmessage.setTextColor(Color.BLACK);
            holder.cmessage.setText(instantMessage.getMessage().trim());
            holder.cname.setText(instantMessage.getCname());

        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}
