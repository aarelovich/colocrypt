package com.colocrypt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ariel on 7/24/15.
 */
public class NumberAdjustView extends LinearLayout {

    private TextView value;

    public NumberAdjustView(Context context, String Name, int minwidth) {
        super(context);
        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Setting the text view
        value = new TextView(context);
        value.setLayoutParams(new ViewGroup.LayoutParams(minwidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        value.setMinimumWidth(minwidth);
        value.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        value.setTextColor(Color.RED);
        float size = value.getTextSize()*(float)(0.5);
        value.setTextSize(size);
        value.setGravity(Gravity.CENTER);

        // Setting the buttons
        TextView title = new TextView(context);
        title.setText(Name);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setTextSize(size);
        title.setTextColor(Color.RED);
        title.setTypeface(Typeface.MONOSPACE);

        // Adding the buttons
        Button plus = new Button(context);
        plus.setText("+");
        plus.setTextColor(Color.RED);
        plus.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        plus.setTextSize(size);
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value.setText(Integer.toString(Integer.valueOf(value.getText().toString()) + 1));
            }
        });

        Button minus = new Button(context);
        minus.setText("-");
        minus.setTextSize(size);
        minus.setTextColor(Color.RED);
        minus.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value.setText(Integer.toString(Integer.valueOf(value.getText().toString()) - 1));
            }
        });

        buttons.addView(value);
        buttons.addView(plus);
        buttons.addView(minus);

        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(title);
        this.addView(buttons);

    }


    public void setValue(int val){
        value.setText(Integer.toString(val));
    }

    public int getValue(){
        return Integer.valueOf(value.getText().toString());
    }
}
