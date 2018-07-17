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

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestButton,mDeclineFriendRequestButton;
    private String mCurrentState;
    private DatabaseReference mFriendRequestDb;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mFriendDatabase;
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
        mDeclineFriendRequestButton = (Button)findViewById(R.id.declineFriendRequestBtn);

        mCurrentState = "not_friends";

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDb = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultpic).into(mProfileImage);
                mFriendRequestDb.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")){
                                mCurrentState = "req_received";
                                mProfileSendRequestButton.setText("Accept Friend Request");
                                mDeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                mDeclineFriendRequestButton.setEnabled(true);
                            } else if (req_type.equals("sent")){
                                mCurrentState = "req_sent";
                                mProfileSendRequestButton.setText("Cancel Friend Request");
                                mDeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineFriendRequestButton.setEnabled(false);
                            }
                        }else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrentState = "friends";
                                        mProfileSendRequestButton.setText("UnFriend");
                                        mDeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        mDeclineFriendRequestButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


            mProfileSendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProfileSendRequestButton.setEnabled(false);
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
                                            mProfileSendRequestButton.setEnabled(true);
                                            mCurrentState = "req_sent";
                                            mProfileSendRequestButton.setText("Cancel Friend Request");
                                            mDeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            mDeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed Sent Request", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });


                    }

                    if (mCurrentState.equals("req_sent")){
                        mFriendRequestDb.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendRequestDb.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mProfileSendRequestButton.setEnabled(true);
                                        mCurrentState = "not_friends";
                                        mProfileSendRequestButton.setText("Send Friend Request");
                                        mDeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        mDeclineFriendRequestButton.setEnabled(false);

                                    }
                                });

                            }
                        });

                    }

//                    if (mCurrentState.equals("friends")){
//                        mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue() .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        mCurrentState = "not_friends";
//                                        mProfileSendRequestButton.setText("Send Friend Request");
//                                    }
//                                });
//                            }
//                        });
//
//                    }

                    if (mCurrentState.equals("req_received")){
                        final String currentDate = DateFormat.getDateInstance().format(new Date());
                        mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mFriendRequestDb.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mFriendRequestDb.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mProfileSendRequestButton.setEnabled(true);
                                                        mCurrentState = "friends";
                                                        mProfileSendRequestButton.setText("UnFriend");
                                                        mDeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                        mDeclineFriendRequestButton.setEnabled(false);

                                                    }
                                                });

                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                }
            });


    }
}
