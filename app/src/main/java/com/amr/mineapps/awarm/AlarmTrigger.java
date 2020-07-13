package com.amr.mineapps.awarm;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class AlarmTrigger extends AppCompatActivity {
    String trackPath;
    Uri trackUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove the title bar of the activity
       //  setFinishOnTouchOutside(false); //to prevent the activity from closing if touched outside of it
        setContentView(R.layout.activity_alarm_trigger);
        TextView alarmLabel = findViewById(R.id.alarm_showed_label);

        if(AlarmSetting.alarmLabel != null ){
            alarmLabel.setText("for: "+AlarmSetting.alarmLabel);
        }

        trackPath = getIntent().getExtras().getString("Track path");
        trackUri = Uri.parse(trackPath);
        Log.v("PATH",trackUri.getPath());

        MediaPlayer mediaPlayer;
        Log.v("FIRST","1");
        try {
            mediaPlayer = MediaPlayer.create(this,trackUri);
            Log.v("2","2");

            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        //AlarmGoes();

    }


    @Override
    public void onBackPressed() {
        //Do nothing
    }


    public void AlarmGoes(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AlarmTrigger.this,trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();


            Button turnOffButton = findViewById(R.id.turnoff_button);
            turnOffButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                  //  mediaPlayer.stop();
                    finish();
                }
            });

    }


}
