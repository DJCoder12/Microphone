package com.example.microphone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class savedRecords extends AppCompatActivity {

    private final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;

    //creates the view of the previous recordings and this allows for the the various previous files to be selected and played back
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        Intent intent = getIntent();
        ArrayList<String> savedFileNames = intent.getStringArrayListExtra("files");

        if(savedFileNames!=null) {
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedFileNames);
            ListView list = (ListView) findViewById(R.id.saved);
            list.setAdapter(itemsAdapter);
        }

        ListView lv = (ListView) findViewById(R.id.saved);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row

                Log.d(LOG_TAG, "YOU TAPPED A LIST ITEM" + value);

                player = new MediaPlayer();
                    try {
                        player.setDataSource(value);
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
            }
        });

    }



}
