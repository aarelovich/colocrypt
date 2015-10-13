package com.colocrypt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ariel on 7/25/15.
 */
public class LoginView extends Activity implements CustomButton.CustomButtonPressedInterface{

    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing the metrics.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Aux.initConstants(metrics, getAssets());
        LinearLayout.LayoutParams params;

        // Title
        TextView title = Aux.AppTitle(this);
        int offset = (int)(Aux.SCREEN_HEIGHT*0.2);

        // Password field
        password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTextColor(Color.GREEN);


        // Creating the list title
        TextView fieldname = new TextView(this);
        fieldname.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
        fieldname.setTextColor(Color.GREEN);
        fieldname.setText("Enter password");
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, offset, 0, 0);
        fieldname.setLayoutParams(params);

        // Button
        int btnw = Aux.SCREEN_WIDTH;
        int btnh = (int)(Aux.SCREEN_HEIGHT*0.1);

        CustomButton login = Aux.makeAppButton(this,this,"DECRYPT",btnw,btnh,0);
        params = (LinearLayout.LayoutParams)login.getLayoutParams();
        params.setMargins(0, Aux.MARGIN_TOP, 0, 0);
        login.setLayoutParams(params);

        LinearLayout global = new LinearLayout(this);
        global.setOrientation(LinearLayout.VERTICAL);
        global.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        global.setBackgroundColor(Color.BLACK);

        global.addView(title);
        global.addView(fieldname);
        global.addView(password);
        global.addView(login);

        setContentView(global);
        checkFiles();

        // For Debug ONLY. Password must obviously been set to aaaa
        //password.setText("aaaa");
        //buttonPressed(0);

    }

    //================= Showing Data once the password has been entered =================
    @Override
    public void buttonPressed(int id) {
        // The first step is loading the preferences
        PasswordData passdata = new PasswordData();

        AESCrypt engine = new AESCrypt(password.getText().toString(),Aux.DATA_FILE);
        if (!engine.getStatus().isEmpty()){
            showError("ERROR: " + engine.getStatus());
            return;
        }

        String message = engine.decrypt();

        if (engine.getStatus().isEmpty()){

//-------------- Time out is commented out ----------------------
//            // Start timer before activity switching
//            long MAX = 300000; // Max value of 5 minutes,
//            CCryptPreferences prefs = new CCryptPreferences(Aux.PREFERENCE_FILE);
//            int mins = prefs.get(CCryptPreferences.MIN_TIMEOUT);
//            int secs = prefs.get(CCryptPreferences.SEC_TIMEOUT);
//            long delay = mins*60000 + secs*1000;
//            delay = Math.min(MAX,delay);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent startActivity = new Intent(LoginView.this, LoginView.class);
//                    startActivity(startActivity);
//                    finish();
//                }
//            }, delay);
//------------------------------------------------------------

            // Data is now shown to the new activity.
            passdata.parseString(message);
            Intent intent = new Intent(this,ShowDataView.class);
            intent.putExtra(Aux.INTENT_CODED_STRING,passdata.generateDataString());
            intent.putExtra(Aux.INTENT_PASSWORD,password.getText().toString());
            startActivity(intent);
            finish();
        }
        else{
            showError("ERROR: Could not decrypt data. Maybe bad Password?");
            return;
        }
    }

    private void checkFiles(){

        // If the file does not exist default values are loaded.
        CCryptPreferences prefs = new CCryptPreferences(Aux.PREFERENCE_FILE);
        // The data should be saved weather the file exists or not.
        prefs.save();

        // Making sure the directory exists
        File f = new File(Aux.WORKING_DIRECTORY);
        if (!f.exists()) {
            // Creating the directory
            if (!f.mkdir()){
                showError("ERROR: Could not create directory: " + Aux.WORKING_DIRECTORY);
                return;
            }
            else{
                // No file is found in this case so the app should go directly to settings in order to set a new password.
                Intent intent = new Intent(this,SettingsView.class);
                intent.putExtra(Aux.INTENT_CODED_STRING,"");
                intent.putExtra(Aux.INTENT_PASSWORD,"");
                startActivity(intent);
                finish();
            }
        }
        else{
            f = new File(Aux.DATA_FILE);
            if (!f.exists()){
                // No file is found in this case so the app should go directly to settings in order to set a new password.
                Intent intent = new Intent(this,SettingsView.class);
                intent.putExtra(Aux.INTENT_CODED_STRING,"");
                intent.putExtra(Aux.INTENT_PASSWORD,"");
                startActivity(intent);
                finish();
            }
        }


    }



    public void showError(String msg){
        Toast t = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void buttonSwiped(int id, boolean totheright) {

    }

    // To disable the back button
    @Override
    public void onBackPressed() {
    }
}
