package com.zidni.chatonfire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zidni.chatonfire.Fragment.APIService;
import com.zidni.chatonfire.Notifications.Client;
import com.zidni.chatonfire.Notifications.Data;
import com.zidni.chatonfire.Notifications.MyResponse;
import com.zidni.chatonfire.Notifications.Sender;
import com.zidni.chatonfire.Notifications.Token;
import com.zidni.chatonfire.adapter.MessageAdapter;
import com.zidni.chatonfire.model.Chat;
import com.zidni.chatonfire.model.Users;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    //declare var;
    ImageView imageView, statusImg;
    TextView username, statusOnOff;
    FirebaseUser fireUser;
    DatabaseReference reference;
    Intent intent;
    EditText editTextMsgSend;
    ImageView send_btn;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    String userid, usernamerecever;
    ValueEventListener seenLitstner;
    String senderName;
    APIService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //full screen window
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //initate var;
        statusImg = findViewById(R.id.status_img_activity_msg);
        statusOnOff = findViewById(R.id.status_activity_msg);
        imageView = findViewById(R.id.imageProfile);
        username = findViewById(R.id.usernameyMsg);
        editTextMsgSend = findViewById(R.id.edittext_chatbox_send);
        send_btn = findViewById(R.id.button_chatbox_send);
        recyclerView = findViewById(R.id.reyclerview_message_list);
//        recyclerView.scrollToPosition( - 1);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        usernamerecever = intent.getStringExtra("username");
        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("MyUsers").child(user2.getUid());

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                senderName = users.getUsername().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                statusOnOff.setText(users.getStatus());
                if (users.getStatus().equals("Online")) {
                    statusImg.setImageResource(R.drawable.status_online);
                }
                if (users.getStatus().equals("Offline")) {
                    statusImg.setImageResource(R.drawable.status_offline);
                }
                if (!users.getImageURL().equals("default")) {
                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageView);

                }
//                if (!users.getImageURL().equals(("default"))){
//                    Glide.with(MessageActivity2.this).load(users.getImageURL()).into(imageView);
//                }
                readmsg(fireUser.getUid(), userid, users.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                String msg = editTextMsgSend.getText().toString();
                if (!msg.equals("")) {
                    sendmessage(fireUser.getUid(), userid, msg, usernamerecever, senderName);
                    editTextMsgSend.setText("");

                }
                else {
                    toastMessage("Please Enter your message first!!");
                }
            }
        });

    }

    private void seenMsgListen(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("ChatList");
        seenLitstner = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fireUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendmessage(String sender, final String receiver, String message, String usernamerecever, String senderName) {
        reference = FirebaseDatabase.getInstance().getReference();

        /*Pushing new data for msges*/
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("receivername", usernamerecever);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("sendername", senderName);
        reference.child("ChatList").push().setValue(hashMap);
        /*For Recent Chats initials*/
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatRecent")
                .child(userid)
                .child(fireUser.getUid());

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRefReceiver.child("id").setValue(fireUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference charRef2 = FirebaseDatabase.getInstance().getReference("ChatRecent").child(fireUser.getUid()).child(userid);
        charRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    charRef2.child("id").setValue(userid);
//                    charRef2.child("receiverid").setValue(chat.getReceiver());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        notifmsg(fireUser.getUid(), userid);
        final String msgesnoti = message;
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("MyUsers").child(fireUser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                sendnotification(receiver,users.getUsername(), msgesnoti );
                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendnotification(final String receiver, final String username, final String msgesnoti) {
        DatabaseReference tokenRef=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokenRef.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data(fireUser.getUid(),
                            R.mipmap.ic_launcher,username+" "+msgesnoti,
                    "New Message", userid);
                    Sender sender=new Sender(data, token.getToken());
                    apiService.seenNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                 if (response.code()==200){
                                     if (response.body().success!=1){
                                         toastMessage("Failed!!!");
                                     }
                                 }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void notifmsg(final String sender, final String reciver) {
        reference = FirebaseDatabase.getInstance().getReference("ChatList");
//        Chat chat=new Chat();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Chat chat2 = snapshot.getValue(Chat.class);
//                    toastMessage(chat2.getReceivername()+currentuser.getEmail());
                    if (!chat2.getReceiver().equals(currentuser.getUid())) {
                        toastMessage("heyyyyyyyyyy");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        if (currentuser.equals(fireUser.getUid())){
//            toastMessage("msg gone");
//        }
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (!chat.getSender().equals(userid)) {
//                        toastMessage("hekko");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


    private void readmsg(final String myid, final String userid, final String imagURL) {
        mChat = new ArrayList<>();
//        final DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("MyUsers").child(fireUser.getUid());
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(boolean.class);
//                if (connected) {
//                    DatabaseReference databaseReference=myRef.child("newValue");
//                    databaseReference.setValue(true);
//                    databaseReference.onDisconnect().setValue(Boolean.FALSE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        reference = FirebaseDatabase.getInstance().getReference("ChatList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat,
                            imagURL);
//                    Collections.reverse(mChat);
//                    messageAdapter.notifyDataSetChanged();

//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
//                    linearLayoutManager.setReverseLayout(true);
//                    linearLayoutManager.setStackFromEnd(true);
//                    recyclerView.setLayoutManager(linearLayoutManager);
//                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(mChat.size() - 1);
//                    recyclerView.smoothScrollToPosition(recyclerView.getItemCount() - 1);
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

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("Online");
//        reference = FirebaseDatabase.getInstance()
//                .getReference("MyUsers").child(fireUser.getUid()).child("newValue");
//        reference.setValue("hello");
//        reference.onDisconnect().setValue("False");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        checkStatus("Offline");

    }

    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("Offline");
        reference.removeEventListener(seenLitstner);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkStatus("Online");
    }


    private void checkStatus(String status) {
        reference = FirebaseDatabase.getInstance()
                .getReference("MyUsers").child(fireUser.getUid());
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("status", status);
        reference.updateChildren(hashMap2);

//        reference.child("status").onDisconnect().setValue("Offline");

    }

    @Override
    protected void onStart() {
        super.onStart();
        seenMsgListen(userid);
    }
}