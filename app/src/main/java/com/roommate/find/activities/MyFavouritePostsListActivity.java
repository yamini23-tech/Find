package com.roommate.find.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roommate.find.R;
import com.roommate.find.common.AppConstants;
import com.roommate.find.models.AddPostDo;
import com.roommate.find.models.FavouriteDo;
import com.roommate.find.models.PostImagesDo;
import com.roommate.find.models.RatingDo;
import com.roommate.find.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyFavouritePostsListActivity extends BaseActivity {

    private LinearLayout llPostsList;
    private RecyclerView rvPostsList;
    private FrameLayout flSearch;
    private TextView tvNoPostsFound;
    private FloatingActionButton fabAddPost;
    private PostListRecyclerAdapter postListRecyclerAdapter;
    private ArrayList<AddPostDo> addPostDos;
    private ArrayList<FavouriteDo> favouriteDos;
    private ArrayList<PostImagesDo> postImagesDos;
    private String userType = "";

    @SuppressLint("RestrictedApi")
    @Override
    public void initialise() {
        llPostsList = (LinearLayout) inflater.inflate(R.layout.activity_posts_list, null);
        addBodyView(llPostsList);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        llFooter.setVisibility(View.GONE);
        tvTitle.setText("MY FAVOURITE POSTS");
        initialiseControls();
        userType = AppConstants.LoggedIn_User_Type;
        flSearch.setVisibility(View.GONE);
        fabAddPost.setVisibility(View.GONE);
        rvPostsList.setLayoutManager(new LinearLayoutManager(MyFavouritePostsListActivity.this, LinearLayoutManager.VERTICAL, false));
        postListRecyclerAdapter = new PostListRecyclerAdapter(MyFavouritePostsListActivity.this, new ArrayList<AddPostDo>());
        rvPostsList.setAdapter(postListRecyclerAdapter);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(context instanceof AddPostActivity)){
                    Intent intent = new Intent(MyFavouritePostsListActivity.this, AddPostActivity.class);
                    startActivityForResult(intent, 120);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        getData();
    }

    private void initialiseControls(){
        flSearch                = llPostsList.findViewById(R.id.flSearch);
        fabAddPost              = llPostsList.findViewById(R.id.fabAddPost);
        rvPostsList             = llPostsList.findViewById(R.id.rvPostsList);
        tvNoPostsFound          = llPostsList.findViewById(R.id.tvNoPostsFound);
    }

    @Override
    protected void onResume() {
        AppConstants.selectedTab = 2;
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 && resultCode == 120){
            getData();
        }
    }

    @Override
    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Favourite);
        showLoader();
        databaseReference.orderByChild("userId").equalTo(preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, ""))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        hideLoader();
                        Log.e("Fav Posts Count " ,""+snapshot.getChildrenCount());
                        ArrayList<FavouriteDo> favouriteDos = new ArrayList<>();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            FavouriteDo favouriteDo = postSnapshot.getValue(FavouriteDo.class);
                            favouriteDos.add(favouriteDo);
                            Log.e("Get Data", favouriteDo.toString());
                        }
                        if(favouriteDos!=null && favouriteDos.size() > 0){
                            rvPostsList.setVisibility(View.VISIBLE);
                            tvNoPostsFound.setVisibility(View.GONE);
                            getPostList(favouriteDos);
                        }
                        else {
                            rvPostsList.setVisibility(View.GONE);
                            tvNoPostsFound.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoader();
                        Log.e("The read failed: " ,databaseError.getMessage());
                    }
                });
    }

    private void getPostList(final ArrayList<FavouriteDo> favouriteDos){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Posts);
        showLoader();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideLoader();
                Log.e("Posts Count " ,""+snapshot.getChildrenCount());
                ArrayList<AddPostDo> addPostDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    AddPostDo addPostDo = postSnapshot.getValue(AddPostDo.class);
                    Log.e("Get Data", addPostDo.toString());
                    for (int i=0; i<favouriteDos.size();i++){
                        if (favouriteDos.get(i).postId.equalsIgnoreCase(addPostDo.postId)){
                            addPostDos.add(addPostDo);
                            break;
                        }
                    }
                }

                if(addPostDos.size() > 0){
                    rvPostsList.setVisibility(View.VISIBLE);
                    tvNoPostsFound.setVisibility(View.GONE);
                    postListRecyclerAdapter.refreshAdapter(addPostDos);
                }
                else {
                    rvPostsList.setVisibility(View.GONE);
                    tvNoPostsFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });

    }

    private void getImagesForThisPost(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Posts_Images);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Images Count " ,""+snapshot.getChildrenCount());
                postImagesDos = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    PostImagesDo postImagesDo = postSnapshot.getValue(PostImagesDo.class);
                    postImagesDos.add(postImagesDo);
                    Log.e("Get Data", postImagesDo.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoader();
                Log.e("images read failed:" ,databaseError.getMessage());
            }
        });
    }

    private class PostListRecyclerAdapter extends RecyclerView.Adapter<PostHolder>{

        private Context context;
        private ArrayList<AddPostDo> addPostDos;

        public PostListRecyclerAdapter(Context context, ArrayList<AddPostDo> addPostDos){
            this.context = context;
            this.addPostDos = addPostDos;
        }

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.post_item_cell, parent, false);
            applyFont(convertView);
            return new PostHolder(convertView);
        }
        @Override
        public void onBindViewHolder(@NonNull final PostHolder holder, int position) {
            final AddPostDo addPostDo = addPostDos.get(position);
            holder.tvPostTitle.setText(addPostDo.title);
            holder.tvPostDate.setText(addPostDo.postDate);
            holder.tvPostPrice.setText(addPostDo.rent);
            holder.tvPostAddress.setText(addPostDo.address1);
            holder.tvPostProperties.setText(addPostDo.bedRooms+" - "+addPostDo.unitType);

            if(postImagesDos!=null && postImagesDos.size()>0){
                for (int i=0;i<postImagesDos.size();i++){
                    if (postImagesDos.get(i).postId.equalsIgnoreCase(addPostDo.postId)){
                        if(addPostDo.image1Path.equalsIgnoreCase("")){
                            addPostDo.image1Path =  postImagesDos.get(i).roomImagePath;
                        }
                        else if(addPostDo.image2Path.equalsIgnoreCase("")){
                            addPostDo.image2Path =  postImagesDos.get(i).roomImagePath;
                        }
                        else if(addPostDo.image3Path.equalsIgnoreCase("")){
                            addPostDo.image3Path =  postImagesDos.get(i).roomImagePath;
                        }
                    }
                }
            }

            String imagePath = setPostImage(addPostDo);
            if(!imagePath.equalsIgnoreCase("")){
                Picasso.get().load(imagePath).placeholder(R.drawable.dummy_room)
                        .error(R.drawable.dummy_room).into(holder.ivPost);
            }
            holder.ivFav.setVisibility(View.GONE);
            getRatingForThisPost(addPostDo, holder.rbRating);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyFavouritePostsListActivity.this, PostDetailsActivity.class);
                    addPostDo.postRating = (int) holder.rbRating.getRating();
                    intent.putExtra("addPostDo", addPostDo);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }

        private String setPostImage(AddPostDo addPostDo){
            if(postImagesDos!=null && postImagesDos.size()>0){
                for (int i=0;i<postImagesDos.size();i++){
                    if (postImagesDos.get(i).postId.equalsIgnoreCase(addPostDo.postId)){
                        return postImagesDos.get(i).roomImagePath;
                    }
                }
            }
            return "";
        }

        private boolean isFavouritePost(String postId, ArrayList<FavouriteDo> favouriteDos){
            if(favouriteDos !=null && favouriteDos.size() > 0){
                for (int i=0; i<favouriteDos.size(); i++){
                    if(favouriteDos.get(i).postId.equalsIgnoreCase(postId)){
                        return true;
                    }
                }
            }
            return false;
        }

        private String getFavouritePostId(String postId, ArrayList<FavouriteDo> favouriteDos){
            if(favouriteDos !=null && favouriteDos.size() > 0){
                for (int i=0; i<favouriteDos.size(); i++){
                    if(favouriteDos.get(i).postId.equalsIgnoreCase(postId)){
                        return favouriteDos.get(i).favouriteId;
                    }
                }
            }
            return "";
        }

        private void favouritePost(AddPostDo addPostDo, boolean add, final ImageView ivFav, String favId){
            if(!add){
                showLoader();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference(AppConstants.Table_Favourite);
                String favouriteId = ""+System.currentTimeMillis();
                FavouriteDo favouriteDo = new FavouriteDo(favouriteId, addPostDo.postId, preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, ""));
                databaseReference.child(favouriteId).setValue(favouriteDo).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideLoader();
                                ivFav.setImageResource(R.drawable.favourite_icon);
                                showAppCompatAlert("", "Added post into your favourite list.", "OK", "", "", true);
                            }
                        });
            }
            else {
                showLoader();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child(AppConstants.Table_Favourite).orderByChild("favouriteId").equalTo(favId);
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        hideLoader();
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        ivFav.setImageResource(R.drawable.unfavourite_icon);
                        showAppCompatAlert("", "Removed post userType your favourite list.", "OK", "", "", true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideLoader();
                        Log.e("PostList", "onCancelled", databaseError.toException());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return addPostDos!=null?addPostDos.size():0;
        }

        public void refreshAdapter(ArrayList<AddPostDo> addPostDos) {
            this.addPostDos = addPostDos;
            notifyDataSetChanged();
        }
    }

    private class PostHolder extends RecyclerView.ViewHolder {

        private ImageView ivPost, ivFav;
        private TextView tvPostTitle, tvPostDate, tvPostPrice, tvPostAddress, tvPostProperties;
        private RatingBar rbRating;


        public PostHolder(@NonNull View itemView) {
            super(itemView);
            ivPost                      = itemView.findViewById(R.id.ivPost);
            ivFav                       = itemView.findViewById(R.id.ivFav);
            tvPostTitle                 = itemView.findViewById(R.id.tvPostTitle);
            tvPostDate                  = itemView.findViewById(R.id.tvPostDate);
            tvPostPrice                 = itemView.findViewById(R.id.tvPostPrice);
            tvPostAddress               = itemView.findViewById(R.id.tvPostAddress);
            tvPostProperties            = itemView.findViewById(R.id.tvPostProperties);
            rbRating                    = itemView.findViewById(R.id.rbRating);
        }
    }

    private void getRatingForThisPost(final AddPostDo addPostDo, final RatingBar rbRating){
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
                        if(preferenceUtils.getStringFromPreference(PreferenceUtils.UserId, "").equalsIgnoreCase(ratingDo.userId)){
//                            llFeedback.setVisibility(View.GONE);
//                            llComment.setVisibility(View.GONE);
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

}
