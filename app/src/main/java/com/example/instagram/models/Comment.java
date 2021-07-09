package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_TEXT = "text";

    /* Default constructor required for a ParseObject class. */
    public Comment() {}

    /* Alternative constructor. */
    public Comment(ParseUser user, Post post, String text) {
        setUser(user);
        setPost(post);
        setText(text);
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

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String description) {
        put(KEY_TEXT, description);
    }
}
