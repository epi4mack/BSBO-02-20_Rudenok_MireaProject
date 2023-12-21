package ru.mirea.rudenok.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
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

import java.util.List;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;

import ru.mirea.rudenok.mireaproject.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {
    android.hardware.biometrics.BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ConstraintLayout mMainLayout;

    private static final String TAG = MainActivity2.class.getSimpleName();
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;
    private static final DatabaseReference FirebaseDbRef = FirebaseDatabase.getInstance().getReference();
    private android.hardware.biometrics.BiometricPrompt.AuthenticationCallback authenticationCallback;

    @RequiresApi(api = Build.VERSION_CODES.R)
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

        // lab 4

        String message = "";

        mMainLayout = findViewById(R.id.login);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                message = "На устройстве нет датчика отпечатка пальца.";
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                message = "Датчик отпечатка пальца недоступен. Попробуйте снова позже.";
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                message = "На устройстве нет сохраненных отпечатков.";
                break;
        }

        final String msg = message;
        binding.fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg != "")
                {
                    Toast.makeText(MainActivity2.this, msg, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    TextView login = findViewById(R.id.EmailAuth);
                    Integer l = login.getText().toString().trim().length();

                    if (l == 0)
                    {
                        Toast.makeText(MainActivity2.this, "Введите электронную почту!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        startFingerprintAuth();
                    }

                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticationCallback = new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    TextView login = findViewById(R.id.EmailAuth);

                    if  (!checkEmail(login.getText().toString()))
                    {
                        Toast.makeText(MainActivity2.this, "Неверный адрес электронной почты.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        startActivity(intent);

                        binding.SignUp.setVisibility(View.GONE);
                        binding.EmailAuth.setVisibility(View.GONE);
                        binding.PasswordAuth.setVisibility(View.GONE);
                        binding.SignIn.setVisibility(View.GONE);
                        binding.LabelAuth.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.GONE);
                        binding.fingerprint.setVisibility(View.GONE);

                        Toast.makeText(MainActivity2.this, "Вход выполнен успешно.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(MainActivity2.this, "Неудачная аутентификация", Toast.LENGTH_SHORT).show();
                }
            };

        }

        // lab 4

        binding.SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView login = findViewById(R.id.EmailAuth);
                TextView pass = findViewById(R.id.PasswordAuth);

                Integer l1 = login.getText().toString().trim().length();
                Integer l2 = pass.getText().toString().trim().length();

                if ((l1 == 0) || (l2 == 0))
                {
                    Toast.makeText(MainActivity2.this, "Заполните оба поля!", Toast.LENGTH_SHORT).show();
                    return;
                }

                signIn(login.getText().toString(), pass.getText().toString());
            }
        });

        boolean auto_fill = false;
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

    private void startFingerprintAuth() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        TextView login = findViewById(R.id.EmailAuth);
        String accountName = login.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt = new android.hardware.biometrics.BiometricPrompt.Builder(this)
                    .setTitle("Вход в аккаунт '" + accountName + "'")
                    .setNegativeButton("Отмена", getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt.authenticate(cancellationSignal, getMainExecutor(), authenticationCallback);
        }
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
            binding.fingerprint.setVisibility(View.GONE);

        } else {
            binding.SignUp.setVisibility(View.VISIBLE);
            binding.EmailAuth.setVisibility(View.VISIBLE);
            binding.PasswordAuth.setVisibility(View.VISIBLE);
            binding.SignIn.setVisibility(View.VISIBLE);
            binding.fingerprint.setVisibility(View.VISIBLE);
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
                            Toast.makeText(MainActivity2.this, "Неудачная аутентификация.", Toast.LENGTH_SHORT).show();
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

    public static void save_hash_to_database(String userID, String hash) {
        DatabaseReference userRef = FirebaseDbRef.child(userID);
        userRef.child("Hashed password").setValue(hash);

    }

    private void signIn(String email, String password) {
        TextView login = findViewById(R.id.EmailAuth);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            final String myUserID = "5lvja6kd1DPJn8R5KqnvbAyLxH83";
                            final String hashed_password = SHA256.encrypt_sha256(password);

                            save_hash_to_database(myUserID, hashed_password);

                            updateUI(user);
                        } else {

                            Toast.makeText(MainActivity2.this, "Неудачная аутентификация.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean checkEmail(String email)
    {
        return email.trim().equals("test@test.ru");
    }
}