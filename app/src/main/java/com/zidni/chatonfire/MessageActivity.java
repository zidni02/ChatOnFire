package com.zidni.chatonfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.gesture.GestureLibraries;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zidni.chatonfire.adapter.MessageAdapter;
import com.zidni.chatonfire.model.Chat;
import com.zidni.chatonfire.model.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ImageView imageView;
    TextView username;
    FirebaseUser fireUser;
    DatabaseReference reference;
    Intent intent;
    EditText editTextMsgSend;
    ImageView send_btn;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.imageProfile);
        username = findViewById(R.id.usernameyMsg);
        editTextMsgSend = findViewById(R.id.edittext_chatbox_send);
        send_btn = findViewById(R.id.button_chatbox_send);
        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);





        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        final String usernamerecever = intent.getStringExtra("username");


        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if (users.getImageURL().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(users.getImageURL()).into(imageView);
                }
             readmsg(fireUser.getUid(), userid, users.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editTextMsgSend.getText().toString();
                if (!msg.equals("")) {
                    sendmessage(fireUser.getUid(), userid, msg, usernamerecever);
                    editTextMsgSend.setText("");
                }
            }
        });
    }
    private void sendmessage(String sender, String receiver, String message, String usernamerecever) {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("receiverName", usernamerecever);
        hashMap.put("message", message);
        reference.child("ChatList").push().setValue(hashMap);
    }
    private void readmsg(final String myid, final String userid, final String imagURL){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("ChatList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                    || chat.getReceiver().equals(userid)&&chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                    messageAdapter =new MessageAdapter(MessageActivity.this, mChat,
                            imagURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
//respond to menu item selection
        switch (item.getItemId()) {
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessageActivity.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}