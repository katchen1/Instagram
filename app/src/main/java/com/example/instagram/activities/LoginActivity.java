package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instagram.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if user is already logged in
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // When the login button is clicked, try to log in with the provided username and password
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String username = binding.etUsername.getText().toString();
               String password = binding.etPassword.getText().toString();
               loginUser(username, password);
           }
        });
    }

    /* Tries to log in to the parse database. If success, navigates to main activity. */
    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Toast.makeText(this, "Issue with logging in.", Toast.LENGTH_SHORT).show();
                return;
            }
            goMainActivity();
        });
    }

    /* Navigates to the main activity. */
    private void goMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
