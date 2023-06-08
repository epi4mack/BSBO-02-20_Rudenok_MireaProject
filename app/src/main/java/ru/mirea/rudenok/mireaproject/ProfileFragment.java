package ru.mirea.rudenok.mireaproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.security.keystore.KeyGenParameterSpec;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ru.mirea.rudenok.mireaproject.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String mainKeyAlias;
        try {
            mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        SharedPreferences secureSharedPreferences = null;
        try {
            secureSharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    mainKeyAlias,
                    getActivity().getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        if(!secureSharedPreferences.getString("name", "null").equals("null") && !secureSharedPreferences.getString("surname", "null").equals("null")
                && !secureSharedPreferences.getString("email", "null").equals("null")
                && !secureSharedPreferences.getString("phone_number", "null").equals("null"))

        {
            binding.Name.setText(secureSharedPreferences.getString("name", "null"));
            binding.Surname.setText(secureSharedPreferences.getString("surname", "null"));
            binding.Email.setText(secureSharedPreferences.getString("email", "null"));
            binding.Number.setText(secureSharedPreferences.getString("phone_number", "null"));
        }

        SharedPreferences SSP = secureSharedPreferences;
        binding.Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSP.edit().putString("name", binding.Name.getText().toString()).apply();
                SSP.edit().putString("surname", binding.Surname.getText().toString()).apply();
                SSP.edit().putString("email", binding.Email.getText().toString()).apply();
                SSP.edit().putString("phone_number", binding.Number.getText().toString()).apply();

                Toast.makeText(root.getContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}