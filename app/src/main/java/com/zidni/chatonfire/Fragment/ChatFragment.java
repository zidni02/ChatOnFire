package com.zidni.chatonfire.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zidni.chatonfire.ChatActivity;
import com.zidni.chatonfire.R;
import com.zidni.chatonfire.adapter.UserAdapter;
import com.zidni.chatonfire.model.ChatList;
import com.zidni.chatonfire.model.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFragment extends Fragment {
    private UserAdapter userAdapter;
    private List<Users> mUsers;
    FirebaseUser fuser;
    DatabaseReference reference;
    private List<ChatList> usersList;
    RecyclerView recyclerView;

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycleviewuser_recent_chat);
        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

//                    messageAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatRecent").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    usersList.add(chatList);
                }
                chatRecent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void chatRecent() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);
                    for (ChatList chatList : usersList) {
                        if (users.getId().equals(chatList.getId())) {
                            mUsers.add(users);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
//                Collections.reverse(mUsers);
                userAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}