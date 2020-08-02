package com.roommate.find.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.UserDo;

public class ForgotPasswordActivity extends BaseActivity {

    private LinearLayout llGuestLogin;
    private TextView tvLogin;
    private EditText etEmail, etPassword, etRenterPassword;
    private DatabaseReference mDatabase;

    @Override
    public void initialise() {
        llGuestLogin = (LinearLayout) inflater.inflate(R.layout.forgot_password_login, null);
        addBodyView(llGuestLogin);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);

        tvLogin                 = llGuestLogin.findViewById(R.id.tvLogin);
        etEmail                 = llGuestLogin.findViewById(R.id.etEmail);
        etPassword              = llGuestLogin.findViewById(R.id.etPassword);
        etRenterPassword        = llGuestLogin.findViewById(R.id.etRenterPassword);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter email");
                }
                else if(!isValidEmail(etEmail.getText().toString().trim())){
                    showErrorMessage("Please enter valid email");
                }
                else if(etPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please enter password");
                }
                else if(etPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please enter password minimum 6 characters");
                }
                else if(etRenterPassword.getText().toString().trim().equalsIgnoreCase("")){
                    showErrorMessage("Please re-enter password");
                }
                else if(etRenterPassword.getText().toString().trim().length() < 6){
                    showErrorMessage("Please re-enter password minimum 6 characters");
                }
                else if(!etPassword.getText().toString().trim().equalsIgnoreCase(etRenterPassword.getText().toString().trim())){
                    showErrorMessage("Please password and re-enter password are same");
                }
                else {
                    if(isNetworkConnectionAvailable(ForgotPasswordActivity.this)){
                        setNewPassword();
                    }
                    else {
                        showInternetDialog("ForgotPassword");
                    }
                }
            }
        });
    }

    private void setNewPassword(){
        final String userId = etEmail.getText().toString().trim().replace("@", "").replace(".", "");
        showLoader();
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstants.Table_Users);
        Query myTopPostsQuery = mDatabase.orderByChild(userId);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isExist = false;
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        UserDo userDo1 = child.getValue(UserDo.class);
                        Log.d("SignupActivity", "message sender name:" + userDo1.toString());
                        if(userDo1.userId.equalsIgnoreCase(userId)){
                            isExist = true;
                            UserDo userDo2 = new UserDo();
                            userDo2.userId = userDo1.userId;
                            userDo2.name = userDo1.name;
                            userDo2.email = userDo1.email;
                            userDo2.phone = userDo1.phone;
                            userDo2.country = userDo1.country;
                            userDo2.state = userDo1.state;
                            userDo2.city = userDo1.city;
                            userDo2.gender = userDo1.gender;
                            userDo2.password = etPassword.getText().toString().trim();
                            userDo2.profilePicUrl = userDo1.profilePicUrl;
                            setNewPassword(userDo2, mDatabase);
                            break;
                        }
                        // [END_EXCLUDE]
                    }
                }
                if (!isExist) {
                    hideLoader();
                    showErrorMessage("The entered email id does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setNewPassword(UserDo userDo, DatabaseReference databaseReference){
        databaseReference.child(userDo.userId).setValue(userDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        showAppCompatAlert("", "Your new password has been set", "OK", "", "ForgotPassword", false);
                    }
                });
    }

    @Override
    public void getData() {

    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if(from.equalsIgnoreCase("ForgotPassword")){
            Intent intent = new Intent(ForgotPasswordActivity.this, PostsListActivity.class);
            intent.putExtra(AppConstants.User_Type, AppConstants.User);
            AppConstants.LoggedIn_User_Type = AppConstants.User;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }
}
