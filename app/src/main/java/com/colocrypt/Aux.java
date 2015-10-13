package com.colocrypt;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by A. Arelovich
 */
public class Aux {

    // Gradients for custom buttons
    public final static int[] BUTTON_NP_GRAD = new int[]{Color.rgb(0,128,128),Color.rgb(30,30,30)};
    public final static int[] BUTTON_P_GRAD = new int[]{Color.rgb(0,240,240),Color.rgb(150,150,150)};
    public final static int BUTTON_FONT_COLOR = Color.rgb(218, 221, 247);
    public final static int TITLE_COLOR = Color.rgb(74, 13, 147);
    public final static int EDIT_BACKGROUND_COLOR = Color.rgb(73,58,72);
    public final static int DIALOG_BACKGROUND_COLOR = Color.rgb(11,0,86);

    public final static int BUTTON_ID_SETTINGS = 0;
    public final static int BUTTON_ID_LOGOUT = 1;
    public final static int BUTTON_ID_EDITDATA = 2;
    public final static int BUTTON_ID_SAVEPREFS = 3;
    public final static int BUTTON_ID_CANCELPREFS = 4;
    public final static int BUTTON_ID_SAVEDATA = 5;
    public final static int BUTTON_ID_CANCELEDIT = 6;
    public final static int BUTTON_ID_ADD = 7;
    public final static int BUTTON_ID_DELENTRY = 8;
    public final static int BUTTON_ID_GENERATENEWPASSWD = 10;
    public final static int BUTTON_ID_USEGENPASSWD = 11;

    public static final String CCRYPT_DIR_NAME = "ccrypt";
    public static final String CCRYPT_FILE_NAME = "data.aux";
    public static final String CCRYPT_SETTINGS = "prefs.dat";

    public static final String INTENT_CODED_STRING = "code_string";
    public static final String INTENT_PASSWORD = "password";

    // Values that need to be calculated.
    public static int BUTTON_FONT_BASE_SIZE;
    public static float BUTTON_OFFSET;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static Typeface BUTTON_FONT;
    public static int TITLE_FONT_BASE_SIZE;
    public static String PREFERENCE_FILE;
    public static String WORKING_DIRECTORY;
    public static String DATA_FILE;
    public static int MARGIN_TOP;

    // To see if it should restart the other activities.
    public static boolean LOGGED_OUT;

    public static void addText(SpannableStringBuilder text, String str, int color, int size, Typeface font) {
        int start = text.toString().length();
        int end = start + str.length();
        text.append(str);
        text.setSpan(new CustomTypefaceSpan("Font", font), start, end, 0);
        text.setSpan(new AbsoluteSizeSpan(size), start, end, 0);
        text.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }

    public static class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTypeface(newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            paint.setTypeface(newType);
        }

    }

    public static class CustomAdapter extends ArrayAdapter<String> {

        private int fontsize;
        private int color;
        private Typeface typeface;
        private int bgcolor = Color.rgb(50, 50, 50);
        private int selectedColor = Color.rgb(50, 50, 50);
        private int selected = -1;
        private boolean setColor = false;
        private List<String> data;

        public CustomAdapter(Context context, int resource, Typeface tf, int colour, int fsize, List<String> labels) {
            super(context, resource, labels);
            fontsize = fsize;
            color = colour;
            typeface = tf;
            data = labels;
        }

        public void setBackgroundColor(int bg) {
            bgcolor = bg;
            setColor = true;
        }

        public void setSelectedColor(int color) {
            selectedColor = color;
            setColor = true;
        }

        public void setSelectedPos(int p) {
            selected = p;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getView(position, convertView, parent);
            SpannableStringBuilder texter = new SpannableStringBuilder();
            Aux.addText(texter, data.get(position), color, fontsize, typeface);
            if (v != null) {
                v.setText(texter, TextView.BufferType.SPANNABLE);
                v.setGravity(Gravity.CENTER);
                if (setColor) {
                    if (position == selected) v.setBackgroundColor(selectedColor);
                    else v.setBackgroundColor(bgcolor);
                }
            }
            return v;
        }


        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getDropDownView(position, convertView, parent);
            SpannableStringBuilder texter = new SpannableStringBuilder();
            Aux.addText(texter, data.get(position), color, fontsize, typeface);
            if (v != null) {
                v.setText(texter, TextView.BufferType.SPANNABLE);
                if (setColor) {
                    if (position == selected) v.setBackgroundColor(selectedColor);
                    else v.setBackgroundColor(bgcolor);
                }
            }
            return v;
        }

    }

    public static TextView AppTitle (Context context){
        TextView title = new android.widget.TextView(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        SpannableStringBuilder text = new SpannableStringBuilder();
        String titlet = "ColoCrypter";
        Aux.addText(text, titlet, TITLE_COLOR, TITLE_FONT_BASE_SIZE, BUTTON_FONT);
        title.setText(text, android.widget.TextView.BufferType.SPANNABLE);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(Color.GREEN);
        return title;
    }

    public static CustomButton makeAppButton(Context context, CustomButton.CustomButtonPressedInterface cpi, String label, int width, int height, int id){
        CustomButton button = new CustomButton(context,cpi,id);
        button.setText(label);
        button.setTextStyle(BUTTON_FONT_BASE_SIZE, BUTTON_FONT, BUTTON_FONT_COLOR);
        button.changeNotPressedColor(BUTTON_NP_GRAD);
        button.changePressedColor(BUTTON_P_GRAD);
        button.setTextYOffset(BUTTON_OFFSET);
        button.setSize(width, height);
        return button;
    }

    public static void initConstants(DisplayMetrics metrics, AssetManager asm){
        float sw = metrics.widthPixels/metrics.xdpi;
        float K = (149.41176f*2.4409f) / (metrics.ydpi * sw);
        SCREEN_HEIGHT = Math.max(metrics.widthPixels, metrics.heightPixels);
        SCREEN_WIDTH = Math.min(metrics.widthPixels, metrics.heightPixels);
        BUTTON_FONT_BASE_SIZE = (int)(18/K);
        BUTTON_OFFSET = (float)(8/K);
        BUTTON_FONT = Typeface.createFromAsset(asm,"souses.otf");
        TITLE_FONT_BASE_SIZE = (int)(22/K);
        String base = Environment.getExternalStorageDirectory().getPath();
        WORKING_DIRECTORY = base + "/" + Aux.CCRYPT_DIR_NAME;
        DATA_FILE = WORKING_DIRECTORY + "/" + CCRYPT_FILE_NAME;
        PREFERENCE_FILE = WORKING_DIRECTORY + "/" + CCRYPT_SETTINGS;
        MARGIN_TOP = (int)(SCREEN_HEIGHT*0.03);
    }


}
