package com.colocrypt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ariel on 7/24/15.
 */
public class SettingsView extends Activity implements CustomButton.CustomButtonPressedInterface{

    private CCryptPreferences prefs;
    private List<NumberAdjustView> editors;
    private String tempData;
    private String epassword;
    private EditText password;
    private EditText cpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int minwidth = Aux.SCREEN_WIDTH /2;
        int btnheight = (int)(Aux.SCREEN_HEIGHT*0.1);
        int btnwidth = (int)(Aux.SCREEN_WIDTH*0.5);

        // Title Text View
        TextView title = Aux.AppTitle(this);

        // Creating the global layout
        LinearLayout global = new LinearLayout(this);
        global.setOrientation(LinearLayout.VERTICAL);
        global.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        global.addView(title);
        global.setBackgroundColor(Color.BLACK);

        // Assuming the file exists.
        prefs = new CCryptPreferences(Aux.PREFERENCE_FILE);

        // Adding all values
        editors = new ArrayList<>();
        for (int i = 0; i < CCryptPreferences.NameForPrefs.size(); i++){
            NumberAdjustView nav = new NumberAdjustView(this, CCryptPreferences.NameForPrefs.get(i),minwidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,Aux.MARGIN_TOP,0,0);
            nav.setLayoutParams(params);
            nav.setValue(prefs.get(i));
            editors.add(nav);
            global.addView(nav);
        }

        // Adding the buttons.
        CustomButton savebutton = Aux.makeAppButton(this,this,"SAVE",btnwidth,btnheight,Aux.BUTTON_ID_SAVEPREFS);
        CustomButton cancelbutton = Aux.makeAppButton(this,this,"CANCEL",btnwidth,btnheight,Aux.BUTTON_ID_CANCELPREFS);

        // Password edition fields
        password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTextColor(Color.GREEN);
        password.setBackgroundColor(Color.DKGRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(10,0,10,0);
        password.setLayoutParams(params);

        cpassword = new EditText(this);
        cpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        cpassword.setTextColor(Color.GREEN);
        cpassword.setBackgroundColor(Color.DKGRAY);
        //params.setMargins(10,0,10,0);
        cpassword.setLayoutParams(params);

        TextView view1 = new TextView(this);
        float size = view1.getTextSize()*(float)(0.5);
        view1.setTextColor(Color.RED);
        view1.setTextSize(size);
        view1.setLayoutParams(params);
        view1.setTypeface(Typeface.MONOSPACE);
        view1.setText("Change encryption password");

        TextView view2 = new TextView(this);
        view2.setLayoutParams(params);
        view2.setTextSize(size);
        view2.setTextColor(Color.RED);
        view2.setTypeface(Typeface.MONOSPACE);
        view2.setText("Confirm new encryption password");

        global.addView(view1);
        global.addView(password);
        global.addView(view2);
        global.addView(cpassword);

        LinearLayout templ = new LinearLayout(this);
        templ.setOrientation(LinearLayout.HORIZONTAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Aux.MARGIN_TOP, 0, 0);
        templ.setLayoutParams(params);
        templ.addView(savebutton);
        templ.addView(cancelbutton);
        templ.setBackgroundColor(Color.BLACK);

        tempData = getIntent().getStringExtra(Aux.INTENT_CODED_STRING);
        epassword = getIntent().getStringExtra(Aux.INTENT_PASSWORD);

        global.addView(templ);
        setContentView(global);

    }


    @Override
    public void buttonPressed(int id) {

        if (id == Aux.BUTTON_ID_SAVEPREFS){

            // Checking that everything is good with passwords.
            if (epassword.isEmpty() && (password.getText().toString().isEmpty() || cpassword.getText().toString().isEmpty())) {
                showError("A password needs to be set in order to proceed.");
                return;
            }

            // Getting the values
            for (int i = 0; i < editors.size(); i++){
                prefs.set(i,editors.get(i).getValue());
            }

            // Checking font values.
//            int mins = prefs.get(CCryptPreferences.MIN_TIMEOUT);
//            int secs = prefs.get(CCryptPreferences.SEC_TIMEOUT);
//            int total = mins*60 + secs;
//            if (total > 300){
//                showError("The maximum allowed time out is 5 minutes");
//                return;
//            }
//
            if ((prefs.get(CCryptPreferences.FONT_SIZE) < 10) || (prefs.get(CCryptPreferences.FONT_SIZE) > 40)){
                showError("The font size for showing data should be between 10 and 40");
                return;
            }

            // Saving to file.
            prefs.save();

            // Checking if any of the password fields is not empty
            if (!password.getText().toString().isEmpty() || !cpassword.getText().toString().isEmpty()){
                if (password.getText().toString().equals(cpassword.getText().toString())){

                    // Encrypting data
                    AESCrypt engine = new AESCrypt(password.getText().toString(),Aux.DATA_FILE);

                    if (!engine.getStatus().isEmpty()){
                        showError("Could not initialize AES Engine: " + engine.getStatus());
                        return;
                    }

                    engine.encrypt(tempData);

                    if (!engine.getStatus().isEmpty()){
                        showError("Could not encrypt data: " + engine.getStatus());
                        return;
                    }

                    // All is good, new password is saved.
                    epassword = password.getText().toString();

                }
                else{
                    showError("Passwords do no match");
                    password.setText("");
                    cpassword.setText("");
                    return;
                }
            }

        }
        else if (id == Aux.BUTTON_ID_CANCELPREFS) {
            if (epassword.isEmpty()) {
                showError("A password needs to be set in order to proceed.");
                return;
            }
        }

        Intent intent;
        intent = new Intent(this,ShowDataView.class);
        intent.putExtra(Aux.INTENT_PASSWORD,epassword);
        intent.putExtra(Aux.INTENT_CODED_STRING,tempData);
        startActivity(intent);
        finish();
    }

    public void showError(String msg){
        Toast t = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void buttonSwiped(int id, boolean totheright) {
        // NOT USED
    }

    // To disable the back button
    @Override
    public void onBackPressed() {
    }
}
