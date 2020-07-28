package com.roommate.find.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.roommate.find.R;

public class PostsListActivity extends BaseActivity {

    private LinearLayout llPostsList;
    @Override
    public void initialise() {
        llPostsList = (LinearLayout) inflater.inflate(R.layout.activity_posts_list, null);
        addBodyView(llPostsList);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        initialiseControls();

    }

    private void initialiseControls(){

    }

    @Override
    public void getData() {

    }

    @Override
    public void onBackPressed() {
        showAppCompatAlert("", "Do you want ot exit from app?", "Exit", "Cancel", "Exit", true);
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);
        if (from.equalsIgnoreCase("Exit")){
            finish();
        }
    }
}
