package com.example.instagram;

import android.app.Application;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);

        // Initialize the parse application
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("50GBXVQnFLUiUQJoDN6vA0qt5WofLHepioBxnDYr")
                .clientKey("MzB58Px4Xay8dHQHiVRe70UQy1dQ4GSEjIeOHWtx")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
