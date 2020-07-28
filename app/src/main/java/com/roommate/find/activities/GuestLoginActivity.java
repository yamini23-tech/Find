package com.roommate.find.activities;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roommate.find.R;

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
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        tvSignUp                = llGuestLogin.findViewById(R.id.tvSignUp);
        tvGuestLogin            = llGuestLogin.findViewById(R.id.tvGuestLogin);
        etEmail                 = llGuestLogin.findViewById(R.id.etEmail);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestLoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
                    Intent intent = new Intent(GuestLoginActivity.this, PostsListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
            }
        });
    }

    @Override
    public void getData() {

    }
}
