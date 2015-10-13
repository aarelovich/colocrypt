package com.colocrypt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;


public class ShowDataView extends Activity implements CustomButton.CustomButtonPressedInterface{


    private ListView passitems;
    private TextView console;
    private PasswordData passdata;
    private String epasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Console size
        int console_width = Aux.SCREEN_WIDTH;
        int console_height = (int)(Aux.SCREEN_HEIGHT *0.2);
        int list_height = (int)(Aux.SCREEN_HEIGHT *0.5);
        int list_width = console_width;

        // Button size
        int log_and_sets_width = (int)(Aux.SCREEN_WIDTH *0.5);
        int log_and_sets_height = (int)(Aux.SCREEN_HEIGHT *0.1);
        int edit_height = (int)(Aux.SCREEN_HEIGHT *0.1);
        int edit_width = (Aux.SCREEN_WIDTH);


        // Drawing the interface

        // Global layout.
        LinearLayout global = new LinearLayout(this);
        global.setOrientation(LinearLayout.VERTICAL);
        global.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        global.setBackgroundColor(Color.BLACK);

        // Title Text View
        TextView title = Aux.AppTitle(this);

        // Custom button logout
        CustomButton logout = Aux.makeAppButton(this,this,"EXIT",log_and_sets_width,log_and_sets_height,Aux.BUTTON_ID_LOGOUT);

        // Custom button settings and data edition
        CustomButton settings = Aux.makeAppButton(this,this,"SETTINGS",log_and_sets_width,log_and_sets_height,Aux.BUTTON_ID_SETTINGS);
        CustomButton editdata = Aux.makeAppButton(this,this,"EDIT DATA",edit_width,edit_height,Aux.BUTTON_ID_EDITDATA);

        // List view
        CCryptPreferences prefs = new CCryptPreferences(Aux.PREFERENCE_FILE);
        passitems = new ListView(this);
        passitems.setLayoutParams(new LinearLayout.LayoutParams(list_width,list_height));

        // Console
        console = new TextView(this);
        console.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        console.setTextColor(Color.GREEN);
        console.setBackgroundColor(Color.BLACK);
        console.setHeight(console_height);
        console.setWidth(console_width);
        console.setHorizontallyScrolling(true);
        console.setMovementMethod(new ScrollingMovementMethod());
        console.setTypeface(Typeface.MONOSPACE);
        console.setTextSize(prefs.get(CCryptPreferences.FONT_SIZE));

        // Intermediate layouts.
        LinearLayout twobuttons = new LinearLayout(this);
        twobuttons.setOrientation(LinearLayout.HORIZONTAL);
        twobuttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        twobuttons.addView(logout);
        twobuttons.addView(settings);

        LinearLayout allbuttons = new LinearLayout(this);
        allbuttons.setOrientation(LinearLayout.VERTICAL);
        allbuttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        allbuttons.setBackgroundColor(Color.DKGRAY);

        allbuttons.addView(twobuttons);
        allbuttons.addView(editdata);

        global.addView(title);
        global.addView(passitems);
        global.addView(console);
        global.addView(allbuttons);

        setContentView(global);

        // Prevent the screen from turning off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //log("Data passed as parameter is: ");
        String data = getIntent().getStringExtra(Aux.INTENT_CODED_STRING);
        epasswd = getIntent().getStringExtra(Aux.INTENT_PASSWORD);

        passdata = new PasswordData();
        passdata.parseString(data);
        //log(passdata.tableFormatData());

        // Adding the data to the list
        int font_size = prefs.get(CCryptPreferences.FONT_SIZE)*3;
        final Aux.CustomAdapter adapter = new Aux.CustomAdapter(this,
                android.R.layout.simple_spinner_item,
                Typeface.MONOSPACE,
                Aux.BUTTON_FONT_COLOR,
                font_size,
                passdata.getItemList());

        adapter.setBackgroundColor(Color.DKGRAY);
        adapter.setSelectedColor(Color.RED);
        passitems.setAdapter(adapter);
        passitems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPos(position);
                showData(position);
            }
        });


    }


    //============= Showing the data in the console =================
    private void showData(int i){
        console.setText("USERNAME: " + passdata.getUser(i) + "\n" +
                        "PASSWORD: " + passdata.getPassword(i));
    }

    //================= Switching to Edit Data View =================
    private void switchToEditViewData(){

        Intent intent = new Intent(this,EditDataView.class);
        intent.putExtra(Aux.INTENT_CODED_STRING, passdata.generateDataString());
        intent.putExtra(Aux.INTENT_PASSWORD,epasswd);
        startActivity(intent);
        finish();

    }

    //================= Switching to Edit Settings View =================
    private void switchToSettingsView(){

        Intent intent = new Intent(this,SettingsView.class);
        intent.putExtra(Aux.INTENT_CODED_STRING, passdata.generateDataString());
        startActivity(intent);
        finish();

    }

    private void log(String s){
        console.setText(console.getText() + "\n" + s);
    }

    @Override
    public void buttonPressed(int id) {
        switch (id){
            case Aux.BUTTON_ID_EDITDATA:
                switchToEditViewData();
                break;
            case Aux.BUTTON_ID_SETTINGS:
                switchToSettingsView();
                break;
            case Aux.BUTTON_ID_LOGOUT:
                Intent intent = new Intent(this,LoginView.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    public void buttonSwiped(int id, boolean totheright) {
        //Not used.
    }

    // To disable the back button
    @Override
    public void onBackPressed() {
    }

}
