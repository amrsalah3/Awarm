package com.amr.mineapps.awarm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class AlarmRepeat extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // for removing the title bar

        LinearLayout dialogView = new LinearLayout(this);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setBackgroundColor(Color.parseColor("#6a1b9a"));
        dialogView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ArrayList<String> choices = new ArrayList<String>();
        choices.add(0,"Never");
        choices.add(1,"One Week");
        choices.add(2,"Two Weeks");
        choices.add(3,"Three Weeks");
        choices.add(4,"Four Weeks");
        choices.add(5,"Weekly");


        for (String Choices : choices ) {
            final TextView textView = new TextView(this);
            textView.setText(Choices);
            textView.setPadding(35,45,35,45);
            textView.setTextSize(35);
            textView.setTextColor(Color.parseColor("#f3e5f5"));
            dialogView.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent repeatSelected = new Intent();
                    repeatSelected.putExtra("SelectedRepeatition",textView.getText());
                    setResult(RESULT_OK,repeatSelected);
                    finish();
                }
            });
        }



        setContentView(dialogView);

    }


}
