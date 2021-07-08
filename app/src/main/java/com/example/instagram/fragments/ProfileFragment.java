package com.example.instagram.fragments;

import android.util.Log;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileFragment extends PostsFragment {

    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void setLayoutManager() {
        // Set up adapter and layout of recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        adapter = new PostsAdapter(getActivity(), allPosts, 1);
        binding.rvPosts.setLayoutManager(layoutManager);

        // Endless scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);
    }

    @Override
    public void queryPosts(int skip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify type of data
        query.include(Post.KEY_USER); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()); // limit posts to current user's
        query.addDescendingOrder("createdAt"); // order posts by creation date
        query.findInBackground((posts, e) -> { // start async query for posts
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            // Save received posts to list and notify adapter of new data
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}
