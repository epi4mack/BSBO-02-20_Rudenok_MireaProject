package ru.mirea.rudenok.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.rudenok.mireaproject.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = MainActivity2.class.getSimpleName();
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(binding.EmailAuth.getText().toString(), binding.PasswordAuth.getText().toString());
            }
        });

        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.EmailAuth.getText().toString(), binding.PasswordAuth.getText().toString());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);

            binding.SignUp.setVisibility(View.GONE);
            binding.EmailAuth.setVisibility(View.GONE);
            binding.PasswordAuth.setVisibility(View.GONE);
            binding.SignIn.setVisibility(View.GONE);
            binding.LabelAuth.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.GONE);

        } else {
            binding.SignUp.setVisibility(View.VISIBLE);
            binding.EmailAuth.setVisibility(View.VISIBLE);
            binding.PasswordAuth.setVisibility(View.VISIBLE);
            binding.SignIn.setVisibility(View.VISIBLE);
        }
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private boolean validateForm() {
        if(binding.PasswordAuth.getText().toString().length() <6)
        {
            return false;
        }
        return !TextUtils.isEmpty(binding.EmailAuth.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.EmailAuth.getText().toString()).matches();
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }
}