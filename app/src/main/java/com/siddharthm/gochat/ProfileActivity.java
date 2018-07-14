package com.siddharthm.gochat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestButton;
    private String mCurrentState;
    private DatabaseReference mFriendRequestDb;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");
        mProfileName = (TextView)findViewById(R.id.profile_displayname);
        mProfileFriendsCount = (TextView)findViewById(R.id.profile_friendsCount);
        mProfileSendRequestButton = (Button)findViewById(R.id.profile_Send_request_btn);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileStatus = (TextView)findViewById(R.id.profile_status);

        mCurrentState = "not_friends";

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDb = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultpic).into(mProfileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


            mProfileSendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentState.equals("not_friends")) {
                        mFriendRequestDb.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mFriendRequestDb.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                            .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this, "Sent Request Succesfully", Toast.LENGTH_SHORT).show();
                                            mProfileSendRequestButton.setEnabled(false);
                                        }
                                    });

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed Sent Request", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });


                    }
                }
            });


    }
}
