package com.zidni.chatonfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference myDataRef;
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView userEm;
    private static final String TAG = "ChatActivity";
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final TextView myTextBox = findViewById(R.id.textbox);

        myAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };
        userEm = findViewById(R.id.usermail);

//        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference();
//        myRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                FirebaseUser user=myAuth.getCurrentUser();
////                userEm.setText(user.getEmail());
//                userEm.setText(snapshot.child("MyUsers").child(userid).child("username").getValue().toString()+" ;");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        FirebaseUser firebaseUser = myAuth.getCurrentUser();
        String userid = firebaseUser.getUid();

        myDataRef = FirebaseDatabase.getInstance().getReference("MyUsers");
        myDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String username = dataSnapshot.child("username").getValue().toString();
                myTextBox.setText("<  "+dataSnapshot.child("msglist").child("email").getValue().toString()+" >  "+dataSnapshot.child("msglist").child("msg").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                myTextBox.setText("Text Canceled");
            }
        });

    }

    public void sendmessage(View view) {
        FirebaseUser firebaseUser = myAuth.getCurrentUser();
        String userid=firebaseUser.getEmail();
//        DatabaseReference userid = FirebaseDatabase.getInstance().getReference("Myuser").child(uid);
        EditText mysendEdit = (EditText) findViewById(R.id.editText);
        myDataRef.child("msglist").child("msg").setValue(mysendEdit.getText().toString());
        myDataRef.child("msglist").child("email").setValue(userid);
        mysendEdit.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            myAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     *
     * @param message
     */
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
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

