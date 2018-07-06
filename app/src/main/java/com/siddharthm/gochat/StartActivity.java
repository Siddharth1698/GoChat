package com.siddharthm.gochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mRegBtn;
    private Button mLogintoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        mLogintoBtn = (Button)findViewById(R.id.login_to_btn);
        mRegBtn = (Button)findViewById(R.id.startRegBtn);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        mLogintoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login_intent);

            }
        });
    }
}
