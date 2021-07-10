package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.UsersAdapter;
import com.example.instagram.databinding.ActivityLikesBinding;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {

    private final String TAG = "LikesActivity";
    private ActivityLikesBinding binding;
    private Post post; // the post whose likes are shown
    private List<ParseUser> users; // list of all the users who liked the post
    private UsersAdapter adapter; // adapter for the users recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLikesBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        post = getIntent().getParcelableExtra("post");

        // Set up adapter and layout of recycler view
        users = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new UsersAdapter(this, users);
        binding.rvUsers.setLayoutManager(layoutManager);
        binding.rvUsers.setAdapter(adapter);
        queryLikeUsers(0);

        // Endless scrolling
        binding.rvUsers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryLikeUsers(users.size());
            }
        });
    }

    /* Queries the current post's likes 20 at a time and adds their users to the list.
     * Skips the first skip likes. */
    public void queryLikeUsers(int skip) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class); // specify type of data
        query.include(Like.KEY_USER); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Like.KEY_POST, post); // limit to the current post
        query.addDescendingOrder("createdAt"); // order comments by creation date
        query.findInBackground((likes, e) -> { // start async query for comments
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting likes", e);
                return;
            }

            // Save received comments to list and notify adapter of new data
            for (Like like: likes) users.add(like.getUser());
            adapter.notifyDataSetChanged();
        });
    }
}