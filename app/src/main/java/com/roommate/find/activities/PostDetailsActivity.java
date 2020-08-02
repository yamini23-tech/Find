package com.roommate.find.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.common.ScheduleMeetingDialog;
import com.roommate.find.models.AddPostDo;
import com.roommate.find.models.RatingDo;
import com.roommate.find.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostDetailsActivity extends BaseActivity {

    private LinearLayout llPostDetails, llEmail, llPhone, llRating, llAddMeeting, llFeedback, llComment;
    private AddPostDo addPostDo;
    private ViewPager vpImage;
    private TabLayout tlImage;
    private RatingBar rbRating, rbFeedback;
    private EditText etFeedback;
    private RadioButton rbHydro, rbHeating, rbParking, rbPet, rbFurniture, rbWifi;
    private TextView tvPostTitle, tvRent,tvAddress1, tvAddress2, tvUnitType, tvBedRooms, tvPostDate, tvPostedBy, tvPostedEmail, tvPostedPhone, tvSend, tvClose;


    @Override
    public void initialise() {
        llPostDetails = (LinearLayout) inflater.inflate(R.layout.post_details_layout, null);
        addBodyView(llPostDetails);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("POST DETAILS");
        llFooter.setVisibility(View.GONE);
        if(getIntent().hasExtra("addPostDo")){
            addPostDo = (AddPostDo) getIntent().getSerializableExtra("addPostDo");
        }
        initialiseControls();
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.User)){
            llRating.setVisibility(View.VISIBLE);
        }
        else {
            llRating.setVisibility(View.GONE);
        }
        tvPostTitle.setText(addPostDo.title);
        tvRent.setText("Rent : $"+addPostDo.rent);
        tvAddress1.setText(addPostDo.address1);
        tvAddress2.setText(addPostDo.address2+", "+addPostDo.zipCode);
        tvUnitType.setText(addPostDo.unitType);
        tvBedRooms.setText(addPostDo.bedRooms);
        tvPostDate.setText(addPostDo.postDate);
        tvPostedBy.setText(addPostDo.postedName);
        tvPostedEmail.setText(addPostDo.postedEmail);
        tvPostedPhone.setText(addPostDo.postPhone);

        if(!addPostDo.hydro.equalsIgnoreCase("")){
            rbHydro.setChecked(Boolean.parseBoolean(addPostDo.hydro));
        }
        if(!addPostDo.heating.equalsIgnoreCase("")){
            rbHeating.setChecked(Boolean.parseBoolean(addPostDo.heating));
        }
        if(!addPostDo.parking.equalsIgnoreCase("")){
            rbParking.setChecked(Boolean.parseBoolean(addPostDo.parking));
        }
        if(!addPostDo.pet.equalsIgnoreCase("")){
            rbPet.setChecked(Boolean.parseBoolean(addPostDo.pet));
        }
        if(!addPostDo.furniture.equalsIgnoreCase("")){
            rbFurniture.setChecked(Boolean.parseBoolean(addPostDo.furniture));
        }
        if(!addPostDo.wifi.equalsIgnoreCase("")){
            rbWifi.setChecked(Boolean.parseBoolean(addPostDo.wifi));
        }
        ArrayList<String> imagesList = new ArrayList<>();
        if(!addPostDo.image1Path.equalsIgnoreCase("")){
            imagesList.add(addPostDo.image1Path);
        }
        if(!addPostDo.image2Path.equalsIgnoreCase("")){
            imagesList.add(addPostDo.image2Path);
        }
        if(!addPostDo.image3Path.equalsIgnoreCase("")){
            imagesList.add(addPostDo.image3Path);
        }
        if(imagesList.size() == 0){
            imagesList.add(addPostDo.image1Path);
        }
        ImageAdapter imageAdapter = new ImageAdapter(context, imagesList);
        vpImage.setOffscreenPageLimit(1);
        vpImage.setAdapter(imageAdapter);
        tlImage.setupWithViewPager(vpImage);

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = rbFeedback.getRating();
                String comment = etFeedback.getText().toString().trim();
                if(rating < 0){
                    showAppCompatAlert("", "Please give rating", "OK", "", "", true);
                }
                else {
                    if(isNetworkConnectionAvailable(PostDetailsActivity.this)){
                        sendFeedback(rating, comment);
                    }
                    else {
                        showInternetDialog("SendFeedback");
                    }
                }
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addPostDo.postPhone.equalsIgnoreCase("")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + addPostDo.postPhone));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{  addPostDo.postedEmail});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reg: Room Post in Find App");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Can you please share more information regarding room details");
                    emailIntent.setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (Exception ex) {
                    Toast.makeText(PostDetailsActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }


            }
        });
        llAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleMeetingDialog meetingDialog = new ScheduleMeetingDialog(PostDetailsActivity.this);
                meetingDialog.show();
            }
        });
        if(AppConstants.LoggedIn_User_Type.equalsIgnoreCase(AppConstants.User)){
            getRatingForThisPost();
        }
    }

    private void initialiseControls(){
        vpImage                 = llPostDetails.findViewById(R.id.vpImage);
        tlImage                 = llPostDetails.findViewById(R.id.tlImage);
        tvPostTitle             = llPostDetails.findViewById(R.id.tvPostTitle);
        tvRent                  = llPostDetails.findViewById(R.id.tvRent);
        tvAddress1              = llPostDetails.findViewById(R.id.tvAddress1);
        tvAddress2              = llPostDetails.findViewById(R.id.tvAddress2);
        tvUnitType              = llPostDetails.findViewById(R.id.tvUnitType);
        tvBedRooms              = llPostDetails.findViewById(R.id.tvBedRooms);
        tvPostDate              = llPostDetails.findViewById(R.id.tvPostDate);
        tvPostedBy              = llPostDetails.findViewById(R.id.tvPostedBy);
        tvPostedEmail           = llPostDetails.findViewById(R.id.tvPostedEmail);
        tvPostedPhone           = llPostDetails.findViewById(R.id.tvPostedPhone);
        tvSend                  = llPostDetails.findViewById(R.id.tvSend);
        tvClose                 = llPostDetails.findViewById(R.id.tvClose);

        rbHydro                 = llPostDetails.findViewById(R.id.rbHydro);
        rbHeating               = llPostDetails.findViewById(R.id.rbHeating);
        rbParking               = llPostDetails.findViewById(R.id.rbParking);
        rbPet                   = llPostDetails.findViewById(R.id.rbPet);
        rbFurniture             = llPostDetails.findViewById(R.id.rbFurniture);
        rbWifi                  = llPostDetails.findViewById(R.id.rbWifi);
        etFeedback              = llPostDetails.findViewById(R.id.etFeedback);
        rbFeedback              = llPostDetails.findViewById(R.id.rbFeedback);
        rbRating                = llPostDetails.findViewById(R.id.rbRating);

        llEmail                 = llPostDetails.findViewById(R.id.llEmail);
        llPhone                 = llPostDetails.findViewById(R.id.llPhone);
        llRating                = llPostDetails.findViewById(R.id.llRating);
        llAddMeeting            = llPostDetails.findViewById(R.id.llAddMeeting);
        llFeedback              = llPostDetails.findViewById(R.id.llFeedback);
        llComment               = llPostDetails.findViewById(R.id.llComment);

    }
    @Override
    public void getData() {

    }

    private void sendFeedback(float rating, String comment){
        showLoader();
        String userId = preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "");
        RatingDo ratingDo = new RatingDo(""+System.currentTimeMillis(), addPostDo.postId, userId, comment, rating);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Rating);
        databaseReference.child(ratingDo.ratingId).setValue(ratingDo).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
                        llRating.setVisibility(View.GONE);
                        showAppCompatAlert("", "Thankyou for giving your valuable feedback.", "OK", "", "Added", false);
                    }
                });
    }

    private void getRatingForThisPost(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Rating);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideLoader();
                Log.e("Rating Count " ,""+snapshot.getChildrenCount());
                ArrayList<RatingDo> ratingDos = new ArrayList<>();
                float rating = 0, noOfRating = 0;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    RatingDo ratingDo = postSnapshot.getValue(RatingDo.class);
                    ratingDos.add(ratingDo);
                    if(addPostDo.postId.equalsIgnoreCase(ratingDo.postId)){
                        rating = rating + ratingDo.postRating;
                        noOfRating = noOfRating + 1;
                        if(preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "").equalsIgnoreCase(ratingDo.userId)) {
                            llRating.setVisibility(View.GONE);
                        }
                    }

                    Log.e("Get Data", ratingDo.toString());
                }
                rbRating.setRating(rating / noOfRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    private class ImageAdapter extends PagerAdapter {

        private Context context;
        private ArrayList<String> imagesList;
        private LayoutInflater layoutInflater;
        public ImageAdapter(Context context, ArrayList<String> imagesList) {
            this.context = context;
            this.imagesList = imagesList;
            layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagesList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == ((View) o);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layoutInflater.inflate(R.layout.row_slider_image, container, false);
            ImageView ivRoomImage = view.findViewById(R.id.ivRoomImage);
            if (!TextUtils.isEmpty(imagesList.get(position))) {
                Picasso.get().load(imagesList.get(position)).placeholder(R.drawable.dummy_room)
                        .error(R.drawable.dummy_room).into(ivRoomImage);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
