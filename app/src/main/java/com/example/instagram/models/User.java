package com.example.instagram.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String KEY_PHOTO = "photo";

    public User() {super();}

    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PHOTO);
    }

    public void setProfilePhoto(ParseFile parseFile) {
        put(KEY_PHOTO, parseFile);
    }
}
