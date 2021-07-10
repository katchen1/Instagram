package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instagram.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /* When the login button is clicked, log in with the provided username and password. */
    public void gotoLoginOnClick(View v) {
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /* Tries to sign up in the parse database. If success, navigates to main activity. */
    public void signupOnClick(View view) {
        String name = binding.etName.getText().toString();
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Check password confirmation
        if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Password fields must be identical", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the ParseUser
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("name", name);

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e == null) {
                goMainActivity();
            } else {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Navigates to the main activity. */
    private void goMainActivity() {
        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
