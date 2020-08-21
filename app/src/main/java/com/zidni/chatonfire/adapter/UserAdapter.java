package com.zidni.chatonfire.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zidni.chatonfire.MessageActivity;
import com.zidni.chatonfire.R;
import com.zidni.chatonfire.model.Users;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<Users> mUsers;
    private List<Users> mEmail;
    private boolean isChat;

    public UserAdapter(Context context, List<Users> mUsers, boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat=isChat;
    }

    public UserAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());
        holder.email.setText(users.getEmail());
        if (users.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(users.getImageURL()).into(holder.imageView);

        }
        if (isChat){
            if (users.getStatus().equals("Online")){
                holder.statuslayoutOn.setVisibility(View.VISIBLE);
                holder.statuslayoutOff.setVisibility(View.GONE);
            }
            else {
                holder.statuslayoutOff.setVisibility(View.VISIBLE);
                holder.statuslayoutOn.setVisibility(View.GONE);
            }
        }
        else {
            holder.statuslayoutOff.setVisibility(View.GONE);
            holder.statuslayoutOn.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                i.putExtra("username", users.getUsername());
                context.startActivity(i);
                checkStatus("Online");
            }
        });

    }

    private void checkStatus(String status) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference("MyUsers").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("status", status);
        myRef.updateChildren(hashMap2);

    }
    @Override
    public int getItemCount() {

        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView imageView;
        public TextView email;
        public LinearLayout statuslayoutOn;
        public LinearLayout statuslayoutOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userlistadapter);
            imageView = itemView.findViewById(R.id.userimageadapter);
            email=itemView.findViewById(R.id.userlistadapterEmail);
            statuslayoutOn=itemView.findViewById(R.id.linLayStatsonn);
            statuslayoutOff=itemView.findViewById(R.id.linLayStatsOff);
        }
    }
}
