package com.siddharthm.gochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;
    private Toolbar mToolBar;
    private ProgressDialog mRegProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolBar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (TextInputLayout)findViewById(R.id.regDisplayName);
        mEmail = (TextInputLayout)findViewById(R.id.loginEmail);
        mPassword = (TextInputLayout)findViewById(R.id.loginPassword);
        mCreateBtn = (Button)findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if (!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your Account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_new_user(displayName,email,password);
                }


            }
        });

    }

    private void register_new_user(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = mCurrentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",displayName);
                            userMap.put("status","hi there,iam using GoChat");
                            userMap.put("image","default");
                            userMap.put("thumbImage","default");
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                         mRegProgress.dismiss();
                                         Intent main_intent = new Intent(RegisterActivity.this,MainActivity.class);
                                         main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(main_intent);
                                         finish();
                                    }
                                }
                            });


                        } else {
                             mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
