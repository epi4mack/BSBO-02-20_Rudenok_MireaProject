package ru.mirea.rudenok.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.security.NoSuchAlgorithmException;

import ru.mirea.rudenok.mireaproject.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = MainActivity2.class.getSimpleName();
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;
    private static final DatabaseReference FirebaseDbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (checkForRAS())
//        {
//            Toast.makeText(getApplicationContext(), "Обнаружен AnyDesk", Toast.LENGTH_SHORT).show();
//            finishAffinity();
//            System.exit(0);
//        } lab2


        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(binding.EmailAuth.getText().toString(), binding.PasswordAuth.getText().toString());
            }
        });

        boolean auto_fill = true;
        boolean auto_auth = false;

        if (auto_fill)
        {
            binding.EmailAuth.setText("test@test.ru");
            binding.PasswordAuth.setText("123456");

            if (auto_auth)
            {
                binding.SignIn.performClick();
            }
        } // auth options

        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.EmailAuth.getText().toString(), binding.PasswordAuth.getText().toString());
            }
        });
    }

    private boolean checkForRAS() {

        List<PackageInfo> installedPacks = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPacks.size(); i++) {
            PackageInfo p = installedPacks.get(i);
            if(Objects.equals(p.packageName, "com.anydesk.anydeskandroid"))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        String ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        TextView IDview = findViewById(R.id.IDview);
//        IDview.setText("Уникальный идентификатор: " + ID); lab1

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

    public static String get_hashed_password(String password) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());

            byte[] bytes = md.digest();

            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            result = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void save_hash_to_database(String userID, String hash) {
        DatabaseReference userRef = FirebaseDbRef.child(userID);
        userRef.child("Hashed password").setValue(hash);
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

                            final String myUserID = "5lvja6kd1DPJn8R5KqnvbAyLxH83";
                            final String hashed_password = get_hashed_password(password);

                            save_hash_to_database(myUserID, hashed_password);

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