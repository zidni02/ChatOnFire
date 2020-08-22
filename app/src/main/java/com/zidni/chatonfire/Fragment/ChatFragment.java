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
import com.zidni.chatonfire.model.Chat;
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
    private List<String> usersList;
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
//        reference = FirebaseDatabase.getInstance().getReference("ChatRecent").child(fuser.getUid());
        reference = FirebaseDatabase.getInstance().getReference("ChatList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    if (chat.getSender().equals(fuser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());
                    }
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
                    for (String id : usersList) {
                        if (users.getId().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (Users users1 : mUsers) {
                                    if (!users.getId().equals(users1.getId())) {
                                        mUsers.add(users);
                                    }
                                }
                            } else {
                                mUsers.add(users);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
//                Collections.reverse(mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}