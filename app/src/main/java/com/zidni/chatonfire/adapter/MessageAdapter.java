package com.zidni.chatonfire.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zidni.chatonfire.MessageActivity;
import com.zidni.chatonfire.R;
import com.zidni.chatonfire.model.Chat;
import com.zidni.chatonfire.model.Users;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Chat> mChat;
    //    private List<Users> mUsers;
    private String imageURL;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser fuser;

    public MessageAdapter(Context context, List<Chat> mChat, String imageURL) {
        this.context = context;
        this.mChat = mChat;
//        this.mUsers = mUsers;
        this.imageURL = imageURL;
    }

    public MessageAdapter() {
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

//            holder.userreceiver.setVisibility(View.VISIBLE);
//            holder.userreceiver.setText(chat.getReceivername());

        holder.show_msg.setText(chat.getMessage());
        holder.userreceiver.setText(chat.getSendername()+" :");


        if (imageURL.equals("default")) {
            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_img);
        }
        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.isseen.setImageResource(R.drawable.ic_baseline_done_outline_24);
                holder.isseen.setVisibility(View.VISIBLE);
            } else {
                holder.isseen.setImageResource(R.drawable.ic_baseline_done_24);
                holder.isseen.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.isseen.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {

        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_msg;
        public ImageView profile_img;
        public TextView userreceiver;
        public ImageView isseen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_msg = itemView.findViewById(R.id.show_msg);
            profile_img = itemView.findViewById(R.id.profile_pic_conversation);
            userreceiver = itemView.findViewById(R.id.usernamereceiver);
            isseen = itemView.findViewById(R.id.msg_status_seen_unseen);


        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
