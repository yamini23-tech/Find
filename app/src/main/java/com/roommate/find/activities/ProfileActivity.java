package com.roommate.find.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.UserDo;
import com.roommate.find.utils.CircleImageView;
import com.roommate.find.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    private View llProfile;
    private CircleImageView civProfile;
    private EditText etName, etEmail, etContactNumber, etCity, etState, etCountry;
    private ImageView ivEdit;
    private TextView tvUpdate;
    private UserDo userDo;
    private Uri imageUri;

    @Override
    public void initialise() {
        llProfile =  inflater.inflate(R.layout.activity_profile, null);
        addBodyView(llProfile);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.VISIBLE);
        llFooter.setVisibility(View.VISIBLE);
        tvTitle.setText("PROFILE");
        initialiseControls();
        civProfile.setEnabled(false);
        civProfile.setAlpha(0.5f);
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etContactNumber.setEnabled(false);
        etCity.setEnabled(false);
        etState.setEnabled(false);
        etCountry.setEnabled(false);
        tvUpdate.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                civProfile.setAlpha(1f);
                civProfile.setEnabled(true);
                etName.setEnabled(true);
                etEmail.setEnabled(true);
                etContactNumber.setEnabled(true);
                etCity.setEnabled(true);
                etState.setEnabled(true);
                etCountry.setEnabled(true);
                etName.setBackgroundResource(R.drawable.edit_text_bg);
                etEmail.setBackgroundResource(R.drawable.edit_text_bg);
                etContactNumber.setBackgroundResource(R.drawable.edit_text_bg);
                etCity.setBackgroundResource(R.drawable.edit_text_bg);
                etState.setBackgroundResource(R.drawable.edit_text_bg);
                etCountry.setBackgroundResource(R.drawable.edit_text_bg);
                tvUpdate.setVisibility(View.VISIBLE);
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter name");
                }
                else if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else if(etContactNumber.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter contact number");
                }
                else if(etCity.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter city");
                }
                else if(etState.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter province");
                }
                else if(etCountry.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter country");
                }
                else {
                    userDo.name   = etName.getText().toString().trim();
                    userDo.phone  = etContactNumber.getText().toString().trim();
                    userDo.city   = etCity.getText().toString().trim();
                    userDo.state  = etState.getText().toString().trim();
                    userDo.country  = etCountry.getText().toString().trim();
                    updateProfile(userDo);
                }
            }
        });
        civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(ProfileActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(ProfileActivity.this, PERMISSIONS, 1210);
                }
                else {
                    selectProfilePic(110);
                }
            }
        });
    }

    private void initialiseControls(){
        civProfile              = llProfile.findViewById(R.id.civProfile);
        ivEdit                  = llProfile.findViewById(R.id.ivEdit);
        etName                  = llProfile.findViewById(R.id.etName);
        etEmail                 = llProfile.findViewById(R.id.etEmail);
        etContactNumber         = llProfile.findViewById(R.id.etContactNumber);
        etCity                  = llProfile.findViewById(R.id.etCity);
        etState                 = llProfile.findViewById(R.id.etState);
        etCountry               = llProfile.findViewById(R.id.etCountry);
        tvUpdate                = llProfile.findViewById(R.id.tvUpdate);

    }


    @Override
    protected void onResume() {
        AppConstants.selectedTab = 3;
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1210 || requestCode == 1211|| requestCode == 1212) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permissions", "Granted");
                selectProfilePic(requestCode);
            }
            else {
                Log.e("Permission", "Denied");
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(ProfileActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(ProfileActivity.this, PERMISSIONS, 201);
                }
            }
        }
    }

    private void selectProfilePic(int requestCode){
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagePath = "Profile_"+userId+"_" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStorageDirectory(), imagePath);
        imageUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(galleryIntent, "Select an option");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
        startActivityForResult(chooser, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 110 && resultCode == RESULT_OK){
            if(data == null){// from camer
                civProfile.setImageURI(imageUri);
            }
            else {// from file storage
                imageUri = data.getData();
                civProfile.setImageURI(imageUri);
            }
            uploadProfilePic();
        }
    }

    private void uploadProfilePic(){
        showLoader();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId,"");
        final String profileImgPath = AppConstants.Profiles_Storage_Path+userId+"_"+ System.currentTimeMillis();
        storageReference.child(profileImgPath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        hideLoader();
                        String profileImgPath = uri.toString();
                        userDo.profilePicUrl = profileImgPath;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                Log.e("Image Upload", "Exception : "+e.getMessage());
                showToast("Error while uploading : "+e.getMessage());
            }
        });
    }

    private void updateProfile(UserDo userDo){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showToast("Profile data updated.");
                        finish();
                    }
                });
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            hideLoader();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                userDo = postSnapshot.getValue(UserDo.class);
                                Log.e("Get Data", userDo.toString());
                                bindData(userDo);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e(TAG, "Failed to reading email.", databaseError.toException());
                    }
                });
    }

    private void bindData(UserDo userDo){
        try {
            etName.setText(userDo.name);
            etEmail.setText(userDo.email);
            etContactNumber.setText(userDo.phone);
            etCity.setText(userDo.city);
            etState.setText(userDo.state);
            etCountry.setText(userDo.country);
            if(!userDo.profilePicUrl.equalsIgnoreCase("")){
                Picasso.get().load(userDo.profilePicUrl).placeholder(R.drawable.profile_icon)
                        .error(R.drawable.profile_icon).into(civProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
