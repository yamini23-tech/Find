package com.roommate.find.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.MeetingDo;
import com.roommate.find.utils.PreferenceUtils;

import java.util.ArrayList;

public class MeetingsListActivity extends BaseActivity {

    private LinearLayout llMeetings;
    private RecyclerView rvMeetings;
    private TextView tvNoMeetingsFound;
    private MeetingsListAdapter meetingsListAdapter;
    private ArrayList<MeetingDo> meetingDos;

    @Override
    public void initialise() {
        llMeetings = (LinearLayout) inflater.inflate(R.layout.activity_meetings_list, null);
        addBodyView(llMeetings);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("MEETINGS");
        llFooter.setVisibility(View.GONE);
        lockMenu();
        initialiseControls();
        rvMeetings.setLayoutManager(new LinearLayoutManager(MeetingsListActivity.this, LinearLayoutManager.VERTICAL, false));
        meetingsListAdapter = new MeetingsListAdapter(MeetingsListActivity.this, meetingDos = new ArrayList<MeetingDo>());
        rvMeetings.setAdapter(meetingsListAdapter);
        getData();
    }

    private void initialiseControls(){
        rvMeetings                      = llMeetings.findViewById(R.id.rvMeetingsList);
        tvNoMeetingsFound               = llMeetings.findViewById(R.id.tvNoMeetingsFound);
    }

    @Override
    public void getData() {
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Meetings);
        showLoader();
        databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideLoader();
                Log.e("Meetings Count " ,""+snapshot.getChildrenCount());
                meetingDos = new ArrayList<>();
                for (DataSnapshot eventSnapshot: snapshot.getChildren()) {
                    MeetingDo meetingDo = eventSnapshot.getValue(MeetingDo.class);
                    meetingDos.add(meetingDo);
                    Log.e("Get Data", meetingDo.toString());
                }
                if(meetingDos!=null && meetingDos.size() > 0){
                    rvMeetings.setVisibility(View.VISIBLE);
                    tvNoMeetingsFound.setVisibility(View.GONE);
                    meetingsListAdapter.refreshAdapter(meetingDos);
                }
                else {
                    rvMeetings.setVisibility(View.GONE);
                    tvNoMeetingsFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });

    }

    private class MeetingsListAdapter extends RecyclerView.Adapter<MeetingHolder> {

        private Context context;
        private ArrayList<MeetingDo> meetingDos;
        public MeetingsListAdapter(Context context, ArrayList<MeetingDo> meetingDos){
            this.context = context;
            this.meetingDos = meetingDos;
        }

        @NonNull
        @Override
        public MeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.meeting_list_item_cell, parent, false);
            return new MeetingHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull MeetingHolder holder, int position) {
            holder.tvMeetingDate.setText("Meeting Date : "+meetingDos.get(position).meetingDate);
            holder.tvMeetingTime.setText("Meeting Time : "+meetingDos.get(position).meetingTime);
            holder.tvMeetingDescription.setText(meetingDos.get(position).meetingDescription);
        }

        @Override
        public int getItemCount() {
            return meetingDos!=null?meetingDos.size():0;
        }

        public void refreshAdapter(ArrayList<MeetingDo> meetingDos) {
            this.meetingDos = meetingDos;
            notifyDataSetChanged();
        }
    }
    private static class MeetingHolder extends RecyclerView.ViewHolder {

        private TextView tvMeetingDate, tvMeetingTime, tvMeetingDescription;

        public MeetingHolder(@NonNull View itemView) {
            super(itemView);
            tvMeetingDate                 = itemView.findViewById(R.id.tvMeetingDate);
            tvMeetingTime                 = itemView.findViewById(R.id.tvMeetingTime);
            tvMeetingDescription          = itemView.findViewById(R.id.tvMeetingDescription);
        }
    }
}
