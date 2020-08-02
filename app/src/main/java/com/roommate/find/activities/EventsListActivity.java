package com.roommate.find.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.EventDo;
import com.roommate.find.utils.PreferenceUtils;

import java.util.ArrayList;

public class EventsListActivity extends BaseActivity {

    private View llEvents;
    private FloatingActionButton fabAddEvent;
    private RecyclerView rvEventsList;
    private TextView tvNoEventsFound;
    private EventsListAdapter eventsListAdapter;
    private ArrayList<EventDo> eventDos;

    @Override
    public void initialise() {
        llEvents = inflater.inflate(R.layout.activity_events_list, null);
        addBodyView(llEvents);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("EVENTS");
        llFooter.setVisibility(View.GONE);
        lockMenu();
        initialiseControls();
        rvEventsList.setLayoutManager(new LinearLayoutManager(EventsListActivity.this, LinearLayoutManager.VERTICAL, false));
        eventsListAdapter = new EventsListAdapter(EventsListActivity.this, eventDos = new ArrayList<EventDo>());
        rvEventsList.setAdapter(eventsListAdapter);

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsListActivity.this, AddEventsListActivity.class);
                startActivityForResult(intent, 121);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        getData();
    }

    private void initialiseControls(){
        rvEventsList                    = llEvents.findViewById(R.id.rvEventsList);
        tvNoEventsFound                 = llEvents.findViewById(R.id.tvNoEventsFound);
        fabAddEvent                     = llEvents.findViewById(R.id.fabAddEvent);
    }

    @Override
    public void getData() {
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Events);
        showLoader();
        databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideLoader();
                Log.e("Events Count " ,""+snapshot.getChildrenCount());
                eventDos = new ArrayList<>();
                for (DataSnapshot eventSnapshot: snapshot.getChildren()) {
                    EventDo eventDo = eventSnapshot.getValue(EventDo.class);
                    eventDos.add(eventDo);
                    Log.e("Get Data", eventDo.toString());
                }
                if(eventDos!=null && eventDos.size() > 0){
                    rvEventsList.setVisibility(View.VISIBLE);
                    tvNoEventsFound.setVisibility(View.GONE);
                    eventsListAdapter.refreshAdapter(eventDos);
                }
                else {
                    rvEventsList.setVisibility(View.GONE);
                    tvNoEventsFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });

    }

    private class EventsListAdapter extends RecyclerView.Adapter<EventHolder> {

        private Context context;
        private ArrayList<EventDo> eventDos;
        public EventsListAdapter(Context context, ArrayList<EventDo> eventDos){
            this.context = context;
            this.eventDos = eventDos;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.event_list_item_cell, parent, false);
            return new EventHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull EventHolder holder, int position) {
            holder.tvEventName.setText(eventDos.get(position).eventTitle);
            holder.tvEventDate.setText(eventDos.get(position).eventDate);
            holder.tvEventDescription.setText(eventDos.get(position).eventDescription);
        }

        @Override
        public int getItemCount() {
            return eventDos!=null?eventDos.size():0;
        }

        public void refreshAdapter(ArrayList<EventDo> eventDos) {
            this.eventDos = eventDos;
            notifyDataSetChanged();
        }
    }
    private static class EventHolder extends RecyclerView.ViewHolder {
        private TextView tvEventName, tvEventDate, tvEventDescription;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName                 = itemView.findViewById(R.id.tvEventName);
            tvEventDate                = itemView.findViewById(R.id.tvEventDate);
            tvEventDescription          = itemView.findViewById(R.id.tvEventDescription);
        }
    }
}
