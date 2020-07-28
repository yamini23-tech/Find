package com.roommate.find.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.utils.PreferenceUtils;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {

    private LinearLayout llLogin;
    private EditText etEmail, etPassword;
    private TextView tvClickHere, tvSignUp, tvGuestUser, tvLogin;

    @Override
    public void initialise() {
        llLogin = (LinearLayout) inflater.inflate(R.layout.activity_login, null);
        addBodyView(llLogin);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        initialiseControls();

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });
        tvGuestUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GuestLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        tvClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v.getRootView());
                if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else if(etPassword.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else {
                    doLogin();
                }
            }
        });
    }

    private void initialiseControls(){
        etEmail                         = findViewById(R.id.etEmail);
        etPassword                      = findViewById(R.id.etPassword);
        tvClickHere                     = findViewById(R.id.tvClickHere);
        tvSignUp                        = findViewById(R.id.tvSignUp);
        tvGuestUser                     = findViewById(R.id.tvGuestUser);
        tvLogin                         = findViewById(R.id.tvLogin);
    }

    private void doLogin(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Users);
        showLoader();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
                if(dataSnapshot!=null && dataSnapshot.exists()){
                    HashMap hashMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(userId);
                    if (hashMap != null && hashMap.get("userId").toString().equalsIgnoreCase(userId)
                            && hashMap.get("password").toString().equalsIgnoreCase(etPassword.getText().toString().trim())) {

                        preferenceUtils.saveString(PreferenceUtils.UserId, userId);
                        preferenceUtils.saveString(PreferenceUtils.UserName, hashMap.get("name").toString());
                        preferenceUtils.saveString(PreferenceUtils.EmailId, etEmail.getText().toString().trim());
                        preferenceUtils.saveString(PreferenceUtils.Password, etPassword.getText().toString().trim());
                        Intent intent = new Intent(LoginActivity.this, PostsListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                    else {
                        //Username does not exist
                        hideLoader();
                        databaseReference.orderByChild("userId")
                                .equalTo(etEmail.getText().toString().trim()).removeEventListener(this);
//                        etPassword.setText("");
                        showAppCompatAlert("", "The entered email and password are not exist.", "OK", "", "", true);
                    }
                }
                else {
                    //Username does not exist
                    hideLoader();
                    databaseReference.child(AppConstants.Table_Users).orderByChild("userId")
                            .equalTo(etEmail.getText().toString().trim()).removeEventListener(this);
//                    etPassword.setText("");
                    showAppCompatAlert("", "The entered email and password are not exist.", "OK", "", "", true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("LoginActivity", "Failed to reading email.", databaseError.toException());
            }
        });
    }

    @Override
    public void getData() {

    }
}
