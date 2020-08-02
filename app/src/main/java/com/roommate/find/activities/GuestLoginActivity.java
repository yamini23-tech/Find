package com.roommate.find.activities;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;

public class GuestLoginActivity extends BaseActivity {

    private LinearLayout llGuestLogin;
    private TextView tvSignUp, tvGuestLogin;
    private EditText etEmail;

    @Override
    public void initialise() {
        llGuestLogin = (LinearLayout) inflater.inflate(R.layout.activity_guest_login, null);
        addBodyView(llGuestLogin);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        tvSignUp                = llGuestLogin.findViewById(R.id.tvSignUp);
        tvGuestLogin            = llGuestLogin.findViewById(R.id.tvGuestLogin);
        etEmail                 = llGuestLogin.findViewById(R.id.etEmail);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestLoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        tvGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else {
                    if(isNetworkConnectionAvailable(GuestLoginActivity.this)){
                        doGuestLogin();
                    }
                    else {
                        showInternetDialog("GuestLogin");
                    }
                }
            }
        });
    }

    private void doGuestLogin(){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Guests);
        String guestUserId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        databaseReference.child(guestUserId).setValue(etEmail.getText().toString().trim()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        Intent intent = new Intent(GuestLoginActivity.this, PostsListActivity.class);
                        intent.putExtra(AppConstants.User_Type, AppConstants.Guest);
                        AppConstants.LoggedIn_User_Type = AppConstants.Guest;
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                });
    }

    @Override
    public void getData() {

    }
}
