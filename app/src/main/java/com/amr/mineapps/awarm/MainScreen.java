package com.amr.mineapps.awarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;


public class MainScreen extends AppCompatActivity {
    Boolean satB,sunB,monB,tueB,wedB,thuB,friB;
    int getTimePickerHour;
    int getTimePickerMinute;
    int deltaH;
    int deltaM;
    String chosenRepeatetion;
    String trackPath;
    String alarmLabel;

    public final static int ALARM_EDIT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        SharedPreferences alarmPrefernces = getSharedPreferences("Alarm Settings", MODE_PRIVATE);
        alarmPrefernces.getString("Track path",null);
    }

    public void EditAlarm(View view){
        Intent GoAlarmSetting = new Intent(this,AlarmSetting.class);
        startActivityForResult(GoAlarmSetting,ALARM_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == ALARM_EDIT){
            if(resultCode == RESULT_OK){
                Bundle alarmInfo = data.getExtras();
                satB = alarmInfo.getBoolean("Sat");
                sunB = alarmInfo.getBoolean("Sun");
                monB = alarmInfo.getBoolean("Mon");
                tueB = alarmInfo.getBoolean("Tue");
                wedB = alarmInfo.getBoolean("Wed");
                thuB = alarmInfo.getBoolean("Thu");
                friB = alarmInfo.getBoolean("Fri");
                getTimePickerHour = alarmInfo.getInt("Picked hour",00);
                getTimePickerMinute = alarmInfo.getInt("Picked minute",00);
                deltaH = alarmInfo.getInt("Delta H");
                deltaM = alarmInfo.getInt("Delta M");
                chosenRepeatetion = alarmInfo.getString("Repeat choice");
                trackPath = alarmInfo.getString("Track path");
                alarmLabel = alarmInfo.getString("Alarm label","");
                alarmManagerCreation();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor alarmPrefernces = getSharedPreferences("Alarm Settings", MODE_PRIVATE).edit();
        alarmPrefernces.putString("Track path",trackPath);
        alarmPrefernces.commit();
    }

    public void alarmManagerCreation(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent goToReceiver = new Intent(this,AlarmReceiver.class);
        goToReceiver.putExtra("Track path",trackPath);
        PendingIntent goToReceiverPI = PendingIntent.getBroadcast(this,0,goToReceiver,0);
        Calendar alarmClock = Calendar.getInstance();
        alarmClock.set(Calendar.HOUR_OF_DAY,getTimePickerHour);
        alarmClock.set(Calendar.MINUTE,getTimePickerMinute);
        alarmClock.set(Calendar.SECOND,0);
        long diff = Calendar.getInstance().getTimeInMillis() - alarmClock.getTimeInMillis();
        if(diff > 0){
            alarmClock.set(Calendar.HOUR_OF_DAY,getTimePickerHour+24);
        }
        if(diff == 0){
            startActivity(goToReceiver);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarmClock.getTimeInMillis(),goToReceiverPI);

    }



}




