package com.siddharthm.gochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private TextInputLayout mStatusInput;
    private Button mSaveBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id = mFirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mToolBar = (Toolbar)findViewById(R.id.status_Appbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String status_value = getIntent().getStringExtra("status_value");
        mStatusInput = (TextInputLayout)findViewById(R.id.status_input);
        mSaveBtn = (Button)findViewById(R.id.statu_save_btn);
        mStatusInput.getEditText().setText(status_value);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save changes");
                mProgress.show();

                String status = mStatusInput.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            Intent back_intent = new Intent(StatusActivity.this,SettingsActivity.class);
                            startActivity(back_intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Couldnt Save",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


    }
}
