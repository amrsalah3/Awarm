package com.amr.mineapps.awarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;


public class AlarmSetting extends AppCompatActivity {
    Boolean satB,sunB,monB,tueB,wedB,thuB,friB;
    public final static int GO_TO_ALARM_REP_DIA_ID = 0;
    public final static int GO_PICK_RINGTONE = 1;
    int getTimePickerHour;
    int getTimePickerMinute;
    int deltaH;
    int deltaM;
    String chosenRepeatetion;
    Uri trackUri;
    String trackPath;
    String viewedText;
    String trackName;
    static String alarmLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        PreAlarmSettings();


    }

    public void PreAlarmSettings() {
        EditText alarmLabelEditText = findViewById(R.id.label_EditText);

        SharedPreferences alarmSettings = getSharedPreferences("Alarm Settings", MODE_PRIVATE);

        satB = alarmSettings.getBoolean("Sat",false);
        sunB = alarmSettings.getBoolean("Sun",false);
        monB = alarmSettings.getBoolean("Mon",false);
        tueB = alarmSettings.getBoolean("Tue",false);
        wedB = alarmSettings.getBoolean("Wed",false);
        thuB = alarmSettings.getBoolean("Thu",false);
        friB = alarmSettings.getBoolean("Fri",false);

        CheckBox sat = findViewById(R.id.sat_cb);
        sat.setChecked(satB);
        CheckBox sun = findViewById(R.id.sun_cb);
        sun.setChecked(sunB);
        CheckBox mon = findViewById(R.id.mon_cb);
        mon.setChecked(monB);
        CheckBox tue = findViewById(R.id.tue_cb);
        tue.setChecked(tueB);
        CheckBox wed = findViewById(R.id.wed_cb);
        wed.setChecked(wedB);
        CheckBox thu = findViewById(R.id.thu_cb);
        thu.setChecked(thuB);
        CheckBox fri = findViewById(R.id.fri_cb);
        fri.setChecked(friB);


        getTimePickerHour = alarmSettings.getInt("Picked hour",00);
        getTimePickerMinute = alarmSettings.getInt("Picked minute",00);
        TimePicker timePicker = findViewById(R.id.time_picker);
        timePicker.setHour(getTimePickerHour);
        timePicker.setMinute(getTimePickerMinute);
        RemainingTime();
        TimePick();

        viewedText = alarmSettings.getString("Repeat choice", "REPEAT(NEVER)");
        trackName = alarmSettings.getString("Selected track", "Not selected");
        trackPath = alarmSettings.getString("Track path","Non");
        AlarmRepeatSetText();
        TrackNameSetText();

        alarmLabelEditText.setText(alarmSettings.getString("Alarm label", ""));

    }

    public void TimePick() {

        TimePicker timePicker = findViewById(R.id.time_picker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                                @Override
                                                public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                                                    getTimePickerHour = timePicker.getHour();
                                                    getTimePickerMinute = timePicker.getMinute();
                                                    RemainingTime();

                                                }

                                            }
        );

    }

    public void RemainingTime(){
        TimePicker timePicker = findViewById(R.id.time_picker);
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        int selectedHour = timePicker.getHour();
        int selectedMinute = timePicker.getMinute();
        TextView remainingTimeTextView = findViewById(R.id.remaining_time);
        deltaH = selectedHour - currentHour;
        deltaM = selectedMinute - currentMinute;
        if (deltaH < 0) {
            deltaH = -deltaH;
        }
        if (deltaM < 0) {
            deltaM = -deltaM;
        }
        if (selectedHour <= currentHour) {
            deltaH = 24 - deltaH;
        }
        if (selectedMinute < currentMinute) {
            deltaH = deltaH - 1;
            deltaM = 60 - deltaM;
        }
        remainingTimeTextView.setText(deltaH + " Hours " + deltaM + " Minutes " + "left ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode == GO_TO_ALARM_REP_DIA_ID) {
            if (resultCode == RESULT_OK) {
                chosenRepeatetion =  data.getStringExtra("SelectedRepeatition");
                viewedText = "REPEAT(" + chosenRepeatetion + ")";
                AlarmRepeatSetText();

            }
        }

        if (requestCode == GO_PICK_RINGTONE) {
            if (resultCode == RESULT_OK) {
                trackUri = data.getData();
                trackPath = trackUri.toString();
                trackName = data.getData().getLastPathSegment();

                TrackNameSetText();
            }
        }

    }

    public void AlarmRepeatSetText() {
        Button repSelected = (Button) findViewById(R.id.repeat_button);
        repSelected.setText(viewedText);

    }

    public void TrackNameSetText() {
        TextView ringtoneTextView = findViewById(R.id.ringtone_textview);
        ringtoneTextView.setText(trackName);
    }

    public void ChooseRepetition(View view) {
        Intent goToAlarmRepDia = new Intent(this, AlarmRepeat.class);
        startActivityForResult(goToAlarmRepDia, GO_TO_ALARM_REP_DIA_ID);
    }

    public void PickRingtone(View view) {

        Intent goPickRingtone = new Intent(Intent.ACTION_GET_CONTENT);
        goPickRingtone.setType("audio/*");

        try {
            startActivityForResult(Intent.createChooser(goPickRingtone, "Pick Ringtone"), GO_PICK_RINGTONE);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "Please install a file manager to be able to pick a file", Toast.LENGTH_LONG).show();
        }


    }

    public void AlarmIsDone(View view){
        Bundle alarmInfo = new Bundle();
        alarmInfo.putBoolean("Sat",satB);
        alarmInfo.putBoolean("Sun",sunB);
        alarmInfo.putBoolean("Mon",monB);
        alarmInfo.putBoolean("Tue",tueB);
        alarmInfo.putBoolean("Wed",wedB);
        alarmInfo.putBoolean("Thu",thuB);
        alarmInfo.putBoolean("Fri",friB);
        alarmInfo.putInt("Picked hour",getTimePickerHour);
        alarmInfo.putInt("Picked minute",getTimePickerMinute);
        alarmInfo.putInt("Delta H",deltaH);
        alarmInfo.putInt("Delta M",deltaM);
        alarmInfo.putString("Repeat choice",chosenRepeatetion);
        alarmInfo.putString("Track path",trackPath);
        alarmInfo.putString("Alarm label",alarmLabel);
        Intent alarmSettingDone = new Intent();
        alarmSettingDone.putExtras(alarmInfo);
        setResult(RESULT_OK,alarmSettingDone);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckBox sat = findViewById(R.id.sat_cb);
        CheckBox sun = findViewById(R.id.sun_cb);
        CheckBox mon = findViewById(R.id.mon_cb);
        CheckBox tue = findViewById(R.id.tue_cb);
        CheckBox wed = findViewById(R.id.wed_cb);
        CheckBox thu = findViewById(R.id.thu_cb);
        CheckBox fri = findViewById(R.id.fri_cb);

        satB = sat.isChecked();
        sunB = sun.isChecked();
        monB = mon.isChecked();
        tueB = tue.isChecked();
        wedB = wed.isChecked();
        thuB = thu.isChecked();
        friB = fri.isChecked();

        EditText alarmLabelEditText = findViewById(R.id.label_EditText);
        alarmLabel =  alarmLabelEditText.getText().toString();
        TimePicker timePicker = findViewById(R.id.time_picker);
        getTimePickerHour = timePicker.getHour();
        getTimePickerMinute = timePicker.getMinute();

        SharedPreferences.Editor alarmSettings = getSharedPreferences("Alarm Settings", MODE_PRIVATE).edit();
        alarmSettings.putString("Repeat choice", viewedText);
        alarmSettings.putString("Selected track", trackName);
        alarmSettings.putString("Track path",trackPath);
        alarmSettings.putString("Alarm label",alarmLabel);
        alarmSettings.putInt("Picked hour", getTimePickerHour);
        alarmSettings.putInt("Picked minute", getTimePickerMinute);
        alarmSettings.putBoolean("Sat",satB);
        alarmSettings.putBoolean("Sun",sunB);
        alarmSettings.putBoolean("Mon",monB);
        alarmSettings.putBoolean("Tue",tueB);
        alarmSettings.putBoolean("Wed",wedB);
        alarmSettings.putBoolean("Thu",thuB);
        alarmSettings.putBoolean("Fri",friB);


        alarmSettings.commit();


    }


}



