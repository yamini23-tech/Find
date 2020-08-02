package com.roommate.find.activities;

import android.content.Intent;
import android.net.Uri;
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
import com.roommate.find.models.SupportDo;
import com.roommate.find.utils.PreferenceUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SupportActivity extends BaseActivity {

    private LinearLayout llSupport;
    private TextView tvSubmit;
    private EditText etComments;
    private TextView tvSupportPhone;

    @Override
    public void initialise() {
        llSupport = (LinearLayout) inflater.inflate(R.layout.activity_support, null);
        addBodyView(llSupport);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("SUPPORT");
        llFooter.setVisibility(View.GONE);
        lockMenu();

        tvSubmit                = llSupport.findViewById(R.id.tvSubmit);
        etComments              = llSupport.findViewById(R.id.etComments);
        tvSupportPhone          = llSupport.findViewById(R.id.tvSupportPhone);

        tvSupportPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvSupportPhone.getText().toString().trim().equalsIgnoreCase("")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + tvSupportPhone.getText().toString().trim()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etComments.getText().toString().equalsIgnoreCase("")){
                    showErrorMessage("Please enter comments");
                }
                else {
                    postCommentsForSupport();
                }
            }
        });
    }
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    private void postCommentsForSupport(){
        showLoader();
        final String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Support);
        final String supportId = "Support_"+System.currentTimeMillis();
        String commentDate = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+"  "
                +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+decimalFormat.format(Calendar.getInstance().get(Calendar.MONTH)+1)
                + "-" +Calendar.getInstance().get(Calendar.YEAR);
        SupportDo supportDo = new SupportDo(supportId, userId, etComments.getText().toString().trim(), commentDate);
        databaseReference.child(supportId).setValue(supportDo)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideLoader();
                showToast("Your query has been submitted");
                finish();
            }
        });
    }

    @Override
    public void getData() {

    }

}
