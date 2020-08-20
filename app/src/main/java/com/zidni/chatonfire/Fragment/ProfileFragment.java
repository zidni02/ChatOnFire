package com.zidni.chatonfire.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zidni.chatonfire.R;
import com.zidni.chatonfire.model.Users;


public class ProfileFragment extends Fragment {
    ImageView imageView;
    TextView textView;
    DatabaseReference reference;
    FirebaseUser fuser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.imageProfile2);
        textView = view.findViewById(R.id.usernamerProfile);
        textView.setAllCaps(true);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                textView.setText(users.getUsername());
                if (users.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getContext()).load(users.getImageURL()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Inflate the layout for this fragment
        return view;
    }
}