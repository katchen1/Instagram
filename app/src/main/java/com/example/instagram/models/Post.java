package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NUM_LIKES = "numLikes";
    public static final String KEY_NUM_COMMENTS = "numComments";

    /* Default constructor required for a ParseObject class. */
    public Post() {}

    /* Alternative constructor. */
    public Post(ParseUser user, String description, ParseFile image) {
        setUser(user);
        setDescription(description);
        setImage(image);
        setNumLikes(0);
        setNumComments(0);
    }

    /* Getters and setters. */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public Integer getNumLikes() {
        return getInt(KEY_NUM_LIKES);
    }

    public void setNumLikes(Integer numLikes) {
        put(KEY_NUM_LIKES, numLikes);
    }

    public Integer getNumComments() {
        return getInt(KEY_NUM_COMMENTS);
    }

    public void setNumComments(Integer numComments) {
        put(KEY_NUM_COMMENTS, numComments);
    }
}
