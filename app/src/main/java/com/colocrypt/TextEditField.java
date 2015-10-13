package com.colocrypt;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ariel on 7/25/15.
 */
public class TextEditField extends LinearLayout {

    private EditText field;
    private TextView name;

    public TextEditField(Context context, String label) {
        super(context);
        this.setOrientation(VERTICAL);

        name = new TextView(context);
        name.setText(label);
        name.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        field = new EditText(context);
        field.setTypeface(Typeface.MONOSPACE);

        this.addView(name);
        this.addView(field);

    }

    public void setFontSize(float fontsize){
        name.setTextSize(fontsize);
        field.setTextSize(fontsize);
    }

    public void setTextColor(int color){
        name.setTextColor(color);
        field.setTextColor(color);
    }

    public void setFieldBackgroudColor(int color){
        field.setBackgroundColor(color);
    }

    public String getFieldText(){
        return field.getText().toString();
    }

    public void setText(String text){
        field.setText(text);
    }

    public void clear(){
        field.setText("");
    }

}
