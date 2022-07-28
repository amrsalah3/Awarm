package com.narify.awarm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.narify.awarm.models.Alarm;
import com.narify.awarm.adapters.AlarmAdapter;
import com.narify.awarm.R;
import com.narify.awarm.utilities.AlarmUtils;
import com.narify.awarm.utilities.PreferenceUtils;
import com.narify.awarm.utilities.TypeConverterUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainScreen extends BaseActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        AlarmAdapter.ListItemEventListeners {

    public final static int NEW_ALARM_DETAILS_CODE = 0;
    public final static int EDIT_ALARM_DETAILS_CODE = 1;
    private RecyclerView mAlarmRecyclerView;
    private AlarmAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        initList();
        findViewById(R.id.fab_new_alarm).setOnClickListener(view -> openAlarmDetails(null));
    }


    private void initList() {
        mAlarmRecyclerView = findViewById(R.id.rv_alarm_list);
        mAdapter = new AlarmAdapter(PreferenceUtils.getStoredAlarmList(), this);

        mAlarmRecyclerView.setHasFixedSize(true);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlarmRecyclerView.setAdapter(mAdapter);
    }


    /**
     * Start AlarmDetails activity for creating or editing an alarm
     *
     * @param clickedAlarm alarm object corresponding to the click position of the list (if any)
     */
    private void openAlarmDetails(Alarm clickedAlarm) {
        Intent intentAlarmDetails = new Intent(this, AlarmDetails.class);

        int requestCode = NEW_ALARM_DETAILS_CODE;

        if (clickedAlarm != null) {
            requestCode = EDIT_ALARM_DETAILS_CODE;

            intentAlarmDetails.putExtra(
                    getString(R.string.key_intent_alarm_extra),
                    TypeConverterUtils.alarmToJson(clickedAlarm));
        }

        intentAlarmDetails.putExtra(getString(R.string.key_intent_request_code), requestCode);
        startActivityForResult(intentAlarmDetails, requestCode);
    }


    @Override
    public void OnAlarmItemClicked(int position) {
        openAlarmDetails(mAdapter.getList().get(position));
    }

    @Override
    public void OnAlarmItemCheckedChange(int position, boolean isChecked) {
        Alarm alarm = mAdapter.getList().get(position);

        alarm.setActivated(isChecked);

        PreferenceUtils.updateStoredAlarm(alarm);

        if (isChecked) AlarmUtils.scheduleAlarm(alarm);
        else AlarmUtils.cancelAlarm(alarm);
    }

    @Override
    public void OnAlarmItemDeleted(int position) {
        Alarm alarm = mAdapter.getList().get(position);

        mAdapter.getList().remove(position);
        mAdapter.notifyItemRemoved(position);

        PreferenceUtils.removeStoredAlarm(alarm);

        AlarmUtils.cancelAlarm(alarm);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mAdapter.setList(PreferenceUtils.getStoredAlarmList());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_settings_item:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_about_item:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_pref_app_theme)))
            recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}




