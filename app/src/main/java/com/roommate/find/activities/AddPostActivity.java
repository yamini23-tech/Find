package com.roommate.find.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.AddPostDo;
import com.roommate.find.models.PostImagesDo;
import com.roommate.find.utils.PreferenceUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

public class AddPostActivity extends BaseActivity implements View.OnClickListener {

    private View llAddPost;
    private EditText etTitle, etRent, etAddress1, etAddress2, etZipCode;
    private Spinner spUnitType, spBedrooms;
    private RadioButton rbHydro, rbHeating, rbParking, rbPet, rbFurniture, rbWifi;
    private LinearLayout llImages;
    private ImageView ivRoomImg1, ivRoomImg2, ivRoomImg3;
    private TextView tvSubmit;
    private Uri imageUri, imageUri2, imageUri3;
    private String selectedUnitType = "", selectedBedRooms = "";
//    private String image1Path = "", image2Path = "", image3Path = "";

    @Override
    public void initialise() {
        llAddPost =  inflater.inflate(R.layout.activity_add_post, null);
        addBodyView(llAddPost);
        lockMenu();
        tvTitle.setText("POST ROOM");
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        llFooter.setVisibility(View.GONE);
        initialiseControls();

        spUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 0){
                    selectedUnitType = ((TextView)view).getText().toString();
                }
                else {
                    selectedUnitType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spBedrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 0){
                    selectedBedRooms = ((TextView)view).getText().toString();
                }
                else {
                    selectedBedRooms = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateRoom();
            }
        });

        ivRoomImg1.setOnClickListener(this);
        ivRoomImg2.setOnClickListener(this);
        ivRoomImg3.setOnClickListener(this);
    }

    private void initialiseControls(){
        etTitle                     = llAddPost.findViewById(R.id.etTitle);
        etRent                      = llAddPost.findViewById(R.id.etRent);
        etAddress1                  = llAddPost.findViewById(R.id.etAddress1);
        etAddress2                  = llAddPost.findViewById(R.id.etAddress2);
        etZipCode                   = llAddPost.findViewById(R.id.etZipCode);
        spUnitType                  = llAddPost.findViewById(R.id.spUnitType);
        spBedrooms                  = llAddPost.findViewById(R.id.spBedrooms);
        rbHydro                     = llAddPost.findViewById(R.id.rbHydro);
        rbHeating                   = llAddPost.findViewById(R.id.rbHeating);
        rbParking                   = llAddPost.findViewById(R.id.rbParking);
        rbPet                       = llAddPost.findViewById(R.id.rbPet);
        rbFurniture                 = llAddPost.findViewById(R.id.rbFurniture);
        rbWifi                      = llAddPost.findViewById(R.id.rbWifi);
        llImages                    = llAddPost.findViewById(R.id.llImages);
        ivRoomImg1                  = llAddPost.findViewById(R.id.ivRoomImg1);
        ivRoomImg2                  = llAddPost.findViewById(R.id.ivRoomImg2);
        ivRoomImg3                  = llAddPost.findViewById(R.id.ivRoomImg3);
        tvSubmit                    = llAddPost.findViewById(R.id.tvSubmit);
    }

    @Override
    public void onClick(View view) {
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        switch (view.getId()){
            case R.id.ivRoomImg1:
                if(!hasPermissions(AddPostActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddPostActivity.this, PERMISSIONS, 1210);
                }
                else {
                    selectProfilePic(1210);
                }
                break;
            case R.id.ivRoomImg2:
                if(!hasPermissions(AddPostActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddPostActivity.this, PERMISSIONS, 1211);
                }
                else {
                    selectProfilePic(1211);
                }
                break;
            case R.id.ivRoomImg3:
                if(!hasPermissions(AddPostActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddPostActivity.this, PERMISSIONS, 1212);
                }
                else {
                    selectProfilePic(1212);
                }
            break;
        }

    }

    private void validateRoom(){
        if(etTitle.getText().toString().trim().equalsIgnoreCase("")){
            showAppCompatAlert("", "Please enter Title", "OK", "", "", false);
        }
        else if(etRent.getText().toString().trim().equalsIgnoreCase("")
                || Integer.parseInt(etRent.getText().toString().trim()) < 1){
            showAppCompatAlert("", "Please enter Rent", "OK", "", "", false);
        }
        else if(etAddress1.getText().toString().trim().equalsIgnoreCase("")){
            showAppCompatAlert("", "Please enter Address1", "OK", "", "", false);
        }
        else if(etAddress2.getText().toString().trim().equalsIgnoreCase("")){
            showAppCompatAlert("", "Please enter Address2", "OK", "", "", false);
        }
        else if(etZipCode.getText().toString().trim().equalsIgnoreCase("")){
            showAppCompatAlert("", "Please enter Zipcode", "OK", "", "", false);
        }
        else if(selectedUnitType.equalsIgnoreCase("")){
            showAppCompatAlert("", "Please select Unit Type", "OK", "", "", false);
        }
        else if(selectedBedRooms.equalsIgnoreCase("")){
            showAppCompatAlert("", "Please select Bed Rooms", "OK", "", "", false);
        }
        else if(imageUri == null && imageUri2 == null && imageUri3 == null){
            showAppCompatAlert("", "Please upload atleast one image", "OK", "", "", false);
        }
        else {
            AddPostDo addPostDo = new AddPostDo();
            addPostDo.title = etTitle.getText().toString().trim();
            addPostDo.rent  = etRent.getText().toString().trim();
            addPostDo.address1  = etAddress1.getText().toString().trim();
            addPostDo.address2  = etAddress2.getText().toString().trim();
            addPostDo.zipCode   = etZipCode.getText().toString().trim();
            addPostDo.unitType   = selectedUnitType.trim();
            addPostDo.bedRooms   = selectedBedRooms.trim();
            addPostDo.hydro      = rbHydro.isChecked()+"";
            addPostDo.heating      = rbHeating.isChecked()+"";
            addPostDo.parking      = rbParking.isChecked()+"";
            addPostDo.pet          = rbPet.isChecked()+"";
            addPostDo.furniture    = rbFurniture.isChecked()+"";
            addPostDo.wifi         = rbWifi.isChecked()+"";
            addPostDo.postBy       = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
            addPostDo.postedName   = preferenceUtils.getStringFromPreference(PreferenceUtils.UserName, "");
            addPostDo.postedEmail  = preferenceUtils.getStringFromPreference(PreferenceUtils.EmailId, "");
            addPostDo.postPhone    = preferenceUtils.getStringFromPreference(PreferenceUtils.PhoneNo, "");
            addPostDo.postDate     = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+decimalFormat.format(Calendar.getInstance().get(Calendar.MONTH)+1)
                                     + "-" +Calendar.getInstance().get(Calendar.YEAR);
            addPostDo.postRating = 0;
            if(isNetworkConnectionAvailable(AddPostActivity.this)){
                postRoom(addPostDo);
            }
            else {
                showInternetDialog("AddPost");
            }
        }

    }
    private final DecimalFormat decimalFormat = new DecimalFormat("00");
    private void postRoom(AddPostDo addPostDo){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Posts);
        final String postId = addPostDo.postBy+"Post"+System.currentTimeMillis();
        addPostDo.postId = postId;
        databaseReference.child(postId).setValue(addPostDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        hideLoader();
                        if(imageUri != null) {
                            uploadImages(postId, imageUri, 1);
                        }
                        else if(imageUri2 != null) {
                            uploadImages(postId, imageUri2, 2);
                        }
                        else if(imageUri3 != null){
                            uploadImages(postId, imageUri3, 3);
                        }
                    }
                });
    }

    private void uploadImages(final String postId, final Uri imageUri1, final int imagePosition){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId,"");
        final String roomImgPath = AppConstants.Posts_Storage_Path+userId+"_"+ System.currentTimeMillis();
        storageReference.child(roomImgPath).putFile(imageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        hideLoader();
                        String profileImgPath = uri.toString();
                        postRoomImages(roomImgPath.replace("/", "_"), postId, profileImgPath, imagePosition);
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

    private void postRoomImages(String roomImgPathId, final String postId, String roomImagePath, final int imagePosition){
//        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        PostImagesDo postImagesDo = new PostImagesDo(roomImgPathId, userId, postId, roomImagePath);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Posts_Images);
        databaseReference.child(roomImgPathId).setValue(postImagesDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(imagePosition == 1 && imageUri2!=null) {
                            imageUri = null;
                            uploadImages(postId, imageUri2, 2);
                        }
                        else if(imagePosition == 2 && imageUri3 != null) {
                            imageUri2 = null;
                            uploadImages(postId, imageUri3, 3);
                        }
                        else if(imagePosition == 3) {
                            imageUri3 = null;
                            hideLoader();
                            setResult(120);
                            finish();
                        }
                        else {
                            hideLoader();
                            setResult(120);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void getData() {

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
                if(!hasPermissions(AddPostActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(AddPostActivity.this, PERMISSIONS, 201);
                }
            }
        }
    }

    private void selectProfilePic(int requestCode){
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagePath = "";
        if(requestCode == 1210){
            imagePath = "Posts_"+userId+"_1_" + System.currentTimeMillis() + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), imagePath);
            imageUri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        else if(requestCode == 1211){
            imagePath = "Posts_"+userId+"_2_" + System.currentTimeMillis() + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), imagePath);
            imageUri2 = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
        }
        else if(requestCode == 1212){
            imagePath = "Posts_"+userId+"_3_" + System.currentTimeMillis() + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), imagePath);
            imageUri3 = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri3);
        }
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
        if(requestCode == 1210 && resultCode == RESULT_OK){
            if(data == null){// from camer
                ivRoomImg1.setImageURI(imageUri);
            }
            else {// from file storage
                imageUri = data.getData();
                ivRoomImg1.setImageURI(imageUri);
            }
//            image1Path = imageUri.toString();
        }
        else if(requestCode == 1211 && resultCode == RESULT_OK){
            if(data == null){// from camera
                ivRoomImg2.setImageURI(imageUri2);
            }
            else {// from file storage
                imageUri2 = data.getData();
                ivRoomImg2.setImageURI(imageUri2);
            }
//            image2Path = imageUri2.toString();
        }
        else if(requestCode == 1212 && resultCode == RESULT_OK){
            if(data == null){// from camer
                ivRoomImg3.setImageURI(imageUri3);
            }
            else {// from file storage
                imageUri3 = data.getData();
                ivRoomImg3.setImageURI(imageUri3);
            }
//            image3Path = imageUri3.toString();
        }
    }
}
