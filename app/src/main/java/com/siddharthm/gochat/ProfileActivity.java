package com.siddharthm.gochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestButton;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String user_id = getIntent().getStringExtra("user_id");
        mProfileName = (TextView)findViewById(R.id.profile_displayname);
        mProfileFriendsCount = (TextView)findViewById(R.id.profile_friendsCount);
        mProfileSendRequestButton = (Button)findViewById(R.id.profile_Send_request_btn);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileStatus = (TextView)findViewById(R.id.profile_status);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
