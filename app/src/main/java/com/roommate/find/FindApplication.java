package com.roommate.find;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class FindApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
