package com.roommate.find.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.UserDo;

public class SignUpActivity extends BaseActivity {

    private FrameLayout llSignup;
    private LinearLayout llNameEmail, llCountryState, llPassword, llFinish;
    private EditText etName, etEmail, etPhone, etCountry, etCity, etState, etPassword, etRenterPassword;
    private RadioGroup rgGender;
    private DatabaseReference mDatabase;
    private RadioButton rbFemale, rbMale;
    private TextView tvEmailNext, tvEmailBack, tvCountryBack, tvCountryNext, tvPasswordBack, tvPasswordNext, tvFinish;
    private String gender = "";


    @Override
    public void initialise() {
        llSignup = (FrameLayout) inflater.inflate(R.layout.activity_signup, null);
        addBodyView(llSignup);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        initialiseControls();
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        llNameEmail.setVisibility(View.VISIBLE);
        tvEmailBack.setVisibility(View.GONE);
        llCountryState.setVisibility(View.GONE);
        llPassword.setVisibility(View.GONE);
        llFinish.setVisibility(View.GONE);

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbFemale){
                    gender = "Female";
                }
                else if(checkedId == R.id.rbMale){
                    gender = "Male";
                }
            }
        });
        tvEmailNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter name");
                }
                else if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(etPhone.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter phone number");
                }
                else if(etPhone.getText().toString().trim().length() !=10){
                    showErrorMessage("Please enter valid phone number");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter email");
                }
                else {
                    llNameEmail.setVisibility(View.GONE);
                    tvEmailBack.setVisibility(View.GONE);
                    llCountryState.setVisibility(View.VISIBLE);
                    llPassword.setVisibility(View.GONE);
                    llFinish.setVisibility(View.GONE);
                }
            }
        });
        tvCountryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNameEmail.setVisibility(View.VISIBLE);
                tvEmailBack.setVisibility(View.GONE);
                llCountryState.setVisibility(View.GONE);
                llPassword.setVisibility(View.GONE);
                llFinish.setVisibility(View.GONE);
            }
        });
        tvPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNameEmail.setVisibility(View.GONE);
                tvEmailBack.setVisibility(View.GONE);
                llCountryState.setVisibility(View.VISIBLE);
                llPassword.setVisibility(View.GONE);
                llFinish.setVisibility(View.GONE);
            }
        });

        tvCountryNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCountry.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter country");
                }
                else if(etCity.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter city");
                }
                else if(etState.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter province");
                }
                else if(gender.equalsIgnoreCase("")){
                    showErrorMessage("Please select gender");
                }
                else {
                    llNameEmail.setVisibility(View.GONE);
                    tvEmailBack.setVisibility(View.GONE);
                    llCountryState.setVisibility(View.GONE);
                    llPassword.setVisibility(View.VISIBLE);
                    llFinish.setVisibility(View.GONE);
                }
            }
        });

        tvPasswordNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etPassword.getText().toString().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if(etRenterPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please Re-enter password");
                }
                else if(!etPassword.getText().toString().equalsIgnoreCase(etRenterPassword.getText().toString())){
                    showErrorMessage("Please enter password and Re-enter password are same");
                }
                else {
                    if(isNetworkConnectionAvailable(SignUpActivity.this)){
                        doRegistration();
                    }
                    else {
                        showInternetDialog("Signup");
                    }
                }
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, PostsListActivity.class);
                AppConstants.LoggedIn_User_Type = AppConstants.User;
                intent.putExtra(AppConstants.User_Type, AppConstants.User);
                intent.putExtra("Email", etEmail.getText().toString().trim());
                intent.putExtra("Phone", etPhone.getText().toString().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

    }

    private void initialiseControls(){
        llCountryState                      = llSignup.findViewById(R.id.llCountryState);
        llNameEmail                         = llSignup.findViewById(R.id.llNameEmail);
        llPassword                          = llSignup.findViewById(R.id.llPassword);
        llFinish                            = llSignup.findViewById(R.id.llFinish);
        rgGender                            = llSignup.findViewById(R.id.rgGender);
        rbFemale                            = llSignup.findViewById(R.id.rbFemale);
        rbMale                             = llSignup.findViewById(R.id.rbMale);

        etName                              = llSignup.findViewById(R.id.etName);
        etEmail                             = llSignup.findViewById(R.id.etEmail);
        etPhone                             = llSignup.findViewById(R.id.etPhone);
        etCountry                           = llSignup.findViewById(R.id.etCountry);
        etState                             = llSignup.findViewById(R.id.etState);
        etCity                              = llSignup.findViewById(R.id.etCity);
        etPassword                          = llSignup.findViewById(R.id.etPassword);
        etRenterPassword                    = llSignup.findViewById(R.id.etRenterPassword);

        tvEmailNext                         = llSignup.findViewById(R.id.tvEmailNext);
        tvEmailBack                         = llSignup.findViewById(R.id.tvEmailBack);
        tvCountryBack                       = llSignup.findViewById(R.id.tvCountryBack);
        tvCountryNext                       = llSignup.findViewById(R.id.tvCountryNext);
        tvPasswordBack                      = llSignup.findViewById(R.id.tvPasswordBack);
        tvPasswordNext                      = llSignup.findViewById(R.id.tvPasswordNext);
        tvFinish                            = llSignup.findViewById(R.id.tvFinish);
    }

    @Override
    public void getData() {

    }


    private void postUserSignupData(){
        final String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        final UserDo userDo = new UserDo(userId, etName.getText().toString().trim(), etEmail.getText().toString().trim(), etPhone.getText().toString().trim(),
                etCountry.getText().toString().trim(), etState.getText().toString().trim(), etCity.getText().toString().trim(), gender, etPassword.getText().toString().trim(), "");

        // My top posts by number of stars
        Query myTopPostsQuery = mDatabase.child(AppConstants.Table_Users).orderByChild(userId);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isExist = false;
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        // Extract a Message object from the DataSnapshot
                        UserDo userDo1 = child.getValue(UserDo.class);
                        // Use the Message
                        // [START_EXCLUDE]
                        Log.d("SignupActivity", "message sender name:" + userDo1.toString());
                        if(userDo1.userId.equalsIgnoreCase(userId)){
                            isExist = true;
                            break;
                        }
                        // [END_EXCLUDE]
                    }
                }
                if (isExist) {
                    Toast.makeText(SignUpActivity.this, "Already You Have Member!! Try to Login", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase.child("users").child(userId).setValue(userDo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!
                                    llNameEmail.setVisibility(View.GONE);
                                    tvEmailBack.setVisibility(View.GONE);
                                    llCountryState.setVisibility(View.GONE);
                                    llPassword.setVisibility(View.GONE);
                                    llFinish.setVisibility(View.VISIBLE);
                                    Toast.makeText(SignUpActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void doRegistration(){
        final String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            hideLoader();
                            showAppCompatAlert("", "The entered email already exist, please register with different email", "OK", "", "", true);
                        }
                        else {
                            //Username does not exist
                            databaseReference.orderByChild("userId").equalTo(userId).removeEventListener(this);
                            insertIntoDB(userId, databaseReference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e("SignupActivity", "Failed to reading email.", databaseError.toException());
                    }
                });
    }

    private void insertIntoDB(String userId, DatabaseReference databaseReference){
        final UserDo userDo = new UserDo(userId, etName.getText().toString().trim(), etEmail.getText().toString().trim(), etPhone.getText().toString().trim(),
                etCountry.getText().toString().trim(), etState.getText().toString().trim(), etCity.getText().toString().trim(), gender, etPassword.getText().toString().trim(), "");

        databaseReference.child(userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Congratulations! You have successfully registered.", "OK", "", "Registration", false);
                    }
                });
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("Registration")){
            llNameEmail.setVisibility(View.GONE);
            tvEmailBack.setVisibility(View.GONE);
            llCountryState.setVisibility(View.GONE);
            llPassword.setVisibility(View.GONE);
            llFinish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
