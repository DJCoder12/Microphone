package com.example.microphone;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.MediaRecorder.AudioSource.MIC;
import static android.os.Environment.getExternalStorageDirectory;

//My compiler settings
//Is there a way for me to also call implements or extends to include Thread use
//

import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Java has a calendar that you can use to get the date and time
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private static String fileName = null;
    Boolean isChecked = false;
    Long date=new Date().getTime();
    ArrayList<String> allFiles = new ArrayList<String>();
    boolean savedInstance = false;

    Date currentTime = new Date(Long.valueOf(date));
    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
//All permission handling
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if(grantResults.length > 0) {

                    permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionStore && permissionToRecordAccepted)
                        recordAudio();
                    else {
                        ToggleButton toggle = (ToggleButton) findViewById(R.id.record);
                        toggle.setChecked(!toggle.isChecked());
                    }
                }
        }
    }

    public boolean check (){
        int res = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int res2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return res == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED;

    }

    private void RequestPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO,WRITE_EXTERNAL_STORAGE},1);
    }
//creates the initial page of the app and sets up the toggle button
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.record);
        toggleButton.setTextOff("Record");
        toggleButton.setTextOn("Stop");
        Log.d(LOG_TAG, "ASASASD" + fileName);
        //ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

    }
// This gives allows for the different buttons to be connected to a function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play:
                Log.d(LOG_TAG, "Playback is happening");

                playAudio();
                return true;

            case R.id.view:
                pastView();
                return true;

            case R.id.save:
                saveAudio();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//This creates the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return true;
    }
//not used
    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
//useless function
    private void onRecord(boolean start) {
        if (start) {

            recordAudio();
        } else {
            stopRecording();
        }
    }
//useless function
    private void onPlay(boolean start) {
        if (start) {
            playAudio();
        } else {
            stopPlaying();
        }
    }
//plays the most recent audio file
    private void playAudio() {
        player = new MediaPlayer();
        if(!savedInstance){
            return;
        }
        else {
            try {
                player.setDataSource(fileName);
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }
    }
// stops recording
    private void stopPlaying() {
        player.release();
        player = null;
    }

//This activates the mic, allows for recording, and writes to output file
    private void recordAudio() {
        if (check()) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            fileName = getExternalStorageDirectory().getAbsolutePath();
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            fileName += "/" + timeStamp + ".3gp";
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            View v = findViewById(R.id.working);
            v.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this,R.anim.blink);
            v.setAnimation(a);
            v.animate();
            recorder.start();

        } else {
            RequestPermissions();
        }
    }
//This ends the animation for recording, and stops recording of mic
    private void stopRecording() {
        recorder.stop();
        View v = findViewById(R.id.working);
        v.setVisibility(View.INVISIBLE);
        v.clearAnimation();
        recorder.release();
        recorder = null;
    }
//This is the intent for a the view page
    public void pastView(){
        Intent intent = new Intent(this, savedRecords.class);
        intent.putStringArrayListExtra("files",allFiles);
        startActivity(intent);
    }
//Ensures that null files are not sent to view
    public void saveAudio(){
        if(fileName!=null) {
            allFiles.add(fileName);
        }


    }
// This checks the status of my toggle button
    public void onClick(View view) {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.record);
        isChecked = toggle.isChecked();
        View v = findViewById(R.id.working);
        if (isChecked) {
            Log.d(LOG_TAG, "Record is happening");

            recordAudio();
            savedInstance = true;
           // v.setVisibility(View.VISIBLE);
            //Animation a = AnimationUtils.loadAnimation(this,R.anim.blink);
            //v.setAnimation(a);
            //v.animate();
        } else {
            Log.d(LOG_TAG, "Record is stopped");
            stopRecording();
            //v.setVisibility(View.INVISIBLE);
           // v.clearAnimation();
        }

    }

}







