package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    /* Default constructor required for a ParseObject class. */
    public Like() {}

    /* Alternative constructor. */
    public Like(ParseUser user, Post post) {
        setUser(user);
        setPost(post);
    }

    /* Getters and setters. */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }
}
