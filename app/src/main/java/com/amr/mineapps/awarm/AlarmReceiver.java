package com.amr.mineapps.awarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.net.URI;


public class AlarmReceiver extends BroadcastReceiver {

String trackPath;

    @Override
    public void onReceive(Context context, Intent intent) {

        trackPath = intent.getExtras().getString("Track path");
        Intent goToAlarmTriggger = new Intent(context,AlarmTrigger.class);
        goToAlarmTriggger.putExtra("Track path",trackPath);
       context.startActivity(goToAlarmTriggger);


    }
}
