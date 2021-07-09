package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.ActivityUserDetailBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDetailActivity extends AppCompatActivity {

    private final String TAG = "UserDetailActivity";
    private ActivityUserDetailBinding binding;
    private List<Post> allPosts; // the posts shown in the grid
    private PostsAdapter adapter; // adapter for the grid of posts
    private ParseUser user; // the user whose profile is shown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fill in the user's basic info
        user = getIntent().getParcelableExtra("user");
        ParseFile profileImage = user.getParseFile("photo");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(binding.ivProfilePhoto);
        }
        binding.tvUsernameToolbar.setText(user.getUsername());
        binding.tvName.setText(user.getString("name"));
        binding.tvBio.setText(user.getString("bio"));
        binding.tvPostCount.setText(String.format(Locale.US, "%d", user.getInt("postCount")));
        binding.tvFollowerCount.setText(String.format(Locale.US, "%d", user.getInt("followerCount")));
        binding.tvFollowingCount.setText(String.format(Locale.US, "%d", user.getInt("followingCount")));

        // Set up adapter and layout of recycler view
        allPosts = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        adapter = new PostsAdapter( this, allPosts, 1, null);
        binding.rvPosts.setLayoutManager(layoutManager);
        binding.rvPosts.setAdapter(adapter);
        queryPosts(0);

        // Endless scrolling
        binding.rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        });

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            allPosts.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    /* Queries the user's posts 20 at a time. Skips the first skip items. */
    public void queryPosts(int skip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify type of data
        query.include(Post.KEY_USER); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Post.KEY_USER, user); // limit posts to current user's
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