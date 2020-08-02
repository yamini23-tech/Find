package com.roommate.find.activities;

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
import com.roommate.find.models.EventDo;
import com.roommate.find.utils.PreferenceUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AddEventsListActivity extends BaseActivity {

    private LinearLayout llAddEvents;
    private EditText etEventTitle, etEventDescription;
    private TextView tvAddEvent;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    @Override
    public void initialise() {
        llAddEvents = (LinearLayout) inflater.inflate(R.layout.activity_add_events, null);
        addBodyView(llAddEvents);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("ADD EVENT");
        llFooter.setVisibility(View.GONE);
        lockMenu();
        initialiseControls();
        tvAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEventTitle.getText().toString().trim().equalsIgnoreCase("")){
                    showAppCompatAlert("", "Please enter event title", "OK", "", "", true);
                }
                else {
                    String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
                    EventDo eventDo = new EventDo();
                    eventDo.eventId = "Event_"+eventDo.userId+"_"+System.currentTimeMillis();
                    eventDo.userId = userId;
                    eventDo.eventTitle = etEventTitle.getText().toString().trim();
                    eventDo.eventDescription = etEventDescription.getText().toString().trim();
                    eventDo.eventDate = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+"  "
                            +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+decimalFormat.format(Calendar.getInstance().get(Calendar.MONTH)+1)
                            + "-" +Calendar.getInstance().get(Calendar.YEAR);
                    if(isNetworkConnectionAvailable(AddEventsListActivity.this)){
                        addEvent(eventDo);
                    }
                    else {
                        showInternetDialog("AddEvent");
                    }
                }
            }
        });
    }

    private void initialiseControls(){
        etEventTitle                    = llAddEvents.findViewById(R.id.etEventTitle);
        etEventDescription              = llAddEvents.findViewById(R.id.etEventDescription);
        tvAddEvent                      = llAddEvents.findViewById(R.id.tvAddEvent);
    }

    @Override
    public void getData() {

    }

    private void addEvent(EventDo eventDo){
        showLoader();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Events);
        databaseReference.child(eventDo.eventId).setValue(eventDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        setResult(121);
                        finish();
                    }
                });
    }


}
