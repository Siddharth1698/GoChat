package com.siddharthm.gochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String user_id = getIntent().getStringExtra("user_id");

        mDisplayId = (TextView)findViewById(R.id.profile_display_name);
        mDisplayId.setText(user_id);
    }
}
