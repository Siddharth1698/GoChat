package com.siddharthm.gochat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = (Toolbar)findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersList = (RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,R.layout.users_single_layout
        ,UsersViewHolder.class,
                mDBRef) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                  viewHolder.setName(model.getName());
                  viewHolder.setUserStatus(model.getStatus());
                  viewHolder.setUsersImage(model.getThumbImage(),getApplicationContext());
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView mUserNameView = mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);

        }

        public void setUserStatus(String status) {
            TextView mUserStatus = mView.findViewById(R.id.user_single_status);
            mUserStatus.setText(status);
        }

        public void setUsersImage(String image, Context ctx) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.defaultpic).into(userImageView);
        }
    }
}
