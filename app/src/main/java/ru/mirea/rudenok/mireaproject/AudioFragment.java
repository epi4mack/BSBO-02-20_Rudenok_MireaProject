package ru.mirea.rudenok.mireaproject;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import ru.mirea.rudenok.mireaproject.databinding.ActivityMainBinding;
import ru.mirea.rudenok.mireaproject.databinding.FragmentAudioBinding;
import ru.mirea.rudenok.mireaproject.databinding.FragmentCameraBinding;

public class AudioFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private final String TAG = MainActivity.class.getSimpleName();
    private boolean isWork = false;
    private String fileName = null;
    private Button recordButton = null;
    private Button playButton = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    boolean isRecording = true;
    boolean isPlaying = true;
    private String recordFilePath = null;

    private AppBarConfiguration mAppBarConfiguration;
    private FragmentAudioBinding binding;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentAudioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recordButton = binding.recordButton;
        playButton = binding.playButton;

        playButton.setEnabled(false);
        recordFilePath = (new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "/audiorecordtest.3gp")).getAbsolutePath();

        int audioRecordPermissionStatus = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(android.os.Build.VERSION.SDK_INT > 32) {
            if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
            }
        }
        else
        {
            if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED && storagePermissionStatus == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    recordButton.setText("Прекратить запись");
                    Drawable mic = getResources().getDrawable(R.drawable.microphone);
                    binding.imageView4.setImageDrawable(mic);
                    playButton.setEnabled(false);
                    startRecording();
                } else {
                    Drawable mic = getResources().getDrawable(R.drawable.mutemicrophone);
                    binding.imageView4.setImageDrawable(mic);
                    recordButton.setText("Начать запись");
                    playButton.setEnabled(true);
                    stopRecording();
                }
                isRecording = !isRecording;
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    playButton.setText("Прекратить проигрывание");
                    recordButton.setEnabled(false);
                    startPlaying();
                } else {
                    playButton.setText("Проигрывание");
                    recordButton.setEnabled(true);
                    stopPlaying();
                }
                isPlaying = !isPlaying;
            }
        });
        return  root;
    }
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        recorder.start();
    }
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }
    private void stopPlaying() {
        player.release();
        player = null;
    }
}