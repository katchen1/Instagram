package com.example.instagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("50GBXVQnFLUiUQJoDN6vA0qt5WofLHepioBxnDYr")
                .clientKey("MzB58Px4Xay8dHQHiVRe70UQy1dQ4GSEjIeOHWtx")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
