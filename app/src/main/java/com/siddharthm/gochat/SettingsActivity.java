package com.siddharthm.gochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mCircleImage;
    private Button mChnageImageBtn;
    private Button mChangeStatusBtn;
    private TextView mName;
    private TextView mStatus;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorageRef;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mCircleImage = (CircleImageView)findViewById(R.id.settings_image);
        mName = (TextView)findViewById(R.id.settings_display_name);
        mStatus = (TextView)findViewById(R.id.settings_status);
        mChangeStatusBtn = (Button)findViewById(R.id.settings_status_button);
        mChnageImageBtn = (Button)findViewById(R.id.settings_image_button);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorageRef = FirebaseStorage.getInstance().getReference();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbImage = dataSnapshot.child("thumbImage").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.with(SettingsActivity.this).load(image).into(mCircleImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChangeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this,StatusActivity.class);
                status_intent.putExtra("status_value",status_value);
                startActivity(status_intent);
            }
        });

        mChnageImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);}


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("Changing Profile Image...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                Uri resultUri = result.getUri();
                File thumb_file_path = new File(resultUri.getPath());
                Bitmap thumb_bitmap = new Compressor(this).setMaxHeight(200).setMaxHeight(200).compressToBitmap(thumb_file_path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_data = baos.toByteArray();
                StorageReference filepath = mImageStorageRef.child("ProfileImages").child(mCurrentUser+".jpg");
                final StorageReference thumb_filepath = mImageStorageRef.child("ProfileImages").child("thumbs").child(mCurrentUser + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_data);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    String thumb_download_url = task.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful()){
                                        Map update_hashmap = new HashMap();
                                        update_hashmap.put("image",downloadUrl);
                                        update_hashmap.put("thumbImage",thumb_download_url);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    mProgress.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Profile Pic Changed Succesfully",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }else {
                                        mProgress.dismiss();
                                    }
                                }
                            });


                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                mProgress.dismiss();
            }
        }


    }
}