package com.zidni.chatonfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emaillogin, passLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button loginBtn, signupBtn;
    private static final String TAG = "LoginActivity";
    FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emaillogin = findViewById(R.id.emailEditTex);
        passLogin = findViewById(R.id.passEditTex);
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.signUpAct);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        myAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

                String email_tex = emaillogin.getText().toString();
                String passTex = passLogin.getText().toString();
                if (TextUtils.isEmpty(email_tex) || TextUtils.isEmpty(passTex)) {
                    Toast.makeText(LoginActivity.this, "Please fill up all the field", Toast.LENGTH_LONG).show();

                } else {
                    progressDialog.setMessage("Loging in");
                    progressDialog.show();
                    myAuth.signInWithEmailAndPassword(email_tex, passTex).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                progressDialog.hide();
                                finish();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(LoginActivity.this, "Inavlid!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }

                    });
                }

            }
        });

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

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}