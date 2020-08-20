package com.zidni.chatonfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zidni.chatonfire.R;
import com.zidni.chatonfire.model.Chat;

import java.util.List;

public class ReceivernameAdapter2 extends RecyclerView.Adapter<ReceivernameAdapter2.ViewHolder> {
    private Context context;
    private List<Chat> mChat;

    public ReceivernameAdapter2(Context context, List<Chat> mUsers) {
        this.context = context;
        this.mChat = mChat;
    }

    public ReceivernameAdapter2() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        return new ReceivernameAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chat chat = mChat.get(position);
        holder.username.setText(chat.getReceivername());

    }


    @Override
    public int getItemCount() {

        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernamereceiver);
        }
    }
}
