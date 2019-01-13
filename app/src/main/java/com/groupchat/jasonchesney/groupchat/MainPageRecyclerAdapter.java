package com.groupchat.jasonchesney.groupchat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPageRecyclerAdapter extends FirebaseRecyclerAdapter<GroupModel, MainPageRecyclerAdapter.MainPageViewHolder> {

    private OnItemClickListener listener;
    private deleteItem lisdel;

    public MainPageRecyclerAdapter(@NonNull FirebaseRecyclerOptions<GroupModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainPageViewHolder holder, int position, @NonNull GroupModel model) {
        holder.regroupname.setText(model.getGrouptitle());
        Picasso.get().load(model.getGroupimage()).placeholder(R.drawable.groupimg).into(holder.regroupimage);
    }

    @NonNull
    @Override
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup vvgrp, int viewType) {
        View view = LayoutInflater.from(vvgrp.getContext())
                .inflate(R.layout.mainpage_recyclerview, vvgrp, false);
        return new MainPageViewHolder(view);
    }

    public void delItem(int position){
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }

    class MainPageViewHolder extends RecyclerView.ViewHolder{

        TextView regroupname;
        CircleImageView regroupimage;
        Button delbtn;
        LinearLayout dell;

        public MainPageViewHolder(View itemview) {
            super(itemview);

            regroupname = itemView.findViewById(R.id.group_name);
            regroupimage = itemView.findViewById(R.id.owners_group_image);
            dell = itemview.findViewById(R.id.dellay);

            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            dell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION && lisdel != null) {
                        lisdel.onItemDelete(getSnapshots().getSnapshot(pos));
                    }
                }
            });

        }
    }

    public interface OnItemClickListener{
        void onItemClick(DataSnapshot dataSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface deleteItem{
        void onItemDelete(DataSnapshot dataSnapshot);
    }
    public void setItemDeleteListener(deleteItem listener){
        this.lisdel = listener;
    }
}
