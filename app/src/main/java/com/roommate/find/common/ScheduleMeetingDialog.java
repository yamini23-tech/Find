package com.roommate.find.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roommate.find.R;
import com.roommate.find.activities.BaseActivity;
import com.roommate.find.activities.PostDetailsActivity;
import com.roommate.find.models.MeetingDo;
import com.roommate.find.utils.PreferenceUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;

public class ScheduleMeetingDialog extends Dialog {

    private Context context;
    private String meetingDate = "", meetingTime = "", meetingDescription = "";
    private int year, month, dayOfMonth, hours, minutes;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    public ScheduleMeetingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.schedule_meeting_dialog);
        setUpDialog();
    }
    
    private void setUpDialog() {
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.schedule_meeting_dialog, null);
//        setContentView(dialogView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final EditText etMeetingDescription               = findViewById(R.id.etMeetingDescription);
        final TextView tvMeetingDate                      = findViewById(R.id.tvMeetingDate);
        final TextView tvMeetingTime                      = findViewById(R.id.tvMeetingTime);
        TextView tvClose                            = findViewById(R.id.tvClose);
        TextView tvScheduleMeeting                  = findViewById(R.id.tvScheduleMeeting);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvMeetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                month      = calendar.get(Calendar.MONTH);
                year       = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                        meetingDate = day+"-"+decimalFormat.format(month+1)+"-"+year;
                        tvMeetingDate.setText("Meeting Date : "+meetingDate);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        tvMeetingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                hours      = calendar.get(Calendar.HOUR_OF_DAY);
                minutes    = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        meetingTime = hours+" : "+decimalFormat.format(minutes);
                        tvMeetingTime.setText("Meeting Time : "+meetingTime);
                    }
                }, hours, minutes, true);
                timePickerDialog.show();
            }
        });

        tvScheduleMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(meetingDate.equalsIgnoreCase("")){
                    ((BaseActivity)context).showErrorMessage("Please select date.");
                }
                else if(meetingTime.equalsIgnoreCase("")){
                    ((BaseActivity)context).showErrorMessage("Please select time.");
                }
                else {
                    if (((BaseActivity)context).isNetworkConnectionAvailable(context)) {
                        addMeeting(meetingDate, meetingTime, etMeetingDescription.getText().toString().trim());
                    }
                    else {
                        ((BaseActivity)context).showInternetDialog("AddMeeting");
                    }
                }
            }
        });
    }

    private void addMeeting(String meetingDate, String meetingTime, String meetingDescription){
        ((BaseActivity)context).showLoader();
        String userId = ((BaseActivity)context).preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        String meetingId = "Meeting_"+userId+"_"+System.currentTimeMillis();
        MeetingDo meetingDo = new MeetingDo(meetingId, userId, meetingDate, meetingTime, meetingDescription);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Meetings);
        databaseReference.child(meetingId).setValue(meetingDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ((BaseActivity)context).hideLoader();
                        ((BaseActivity)context).showAppCompatAlert("", "Meeting has been scheduled.", "OK", "", "AddMeeting", false);
                        dismiss();
                    }
                });
    }
}
