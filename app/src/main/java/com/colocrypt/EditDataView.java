package com.colocrypt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.colocrypt.CustomButton.CustomButtonPressedInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ariel on 7/23/15.
 */
public class EditDataView extends Activity implements CustomButtonPressedInterface{

    private ListView namelist;
    private TextView console;
    private PasswordData passdata;
    private CCryptPreferences prefs;
    private List<TextEditField> fields;
    private int selection;

    private final static List<String> fieldLabels = Arrays.asList("Name or description for entry","Username","Password");
    private final static int FIELD_NAME = 0;
    private final static int FIELD_USER = 1;
    private final static int FIELD_PASS = 2;

    // Confirm dialog functions
    private String epassword;
    private String bkpdata;
    private GenerateNewPasswordDialog generator;

    //============================= COSTRUCTOR ===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting the preferences.
        prefs = new CCryptPreferences(Aux.PREFERENCE_FILE);

        // Getting Decrypted data. And Backing it up in case of a cancel.
        String data = getIntent().getStringExtra(Aux.INTENT_CODED_STRING);
        bkpdata = data;
        epassword = getIntent().getStringExtra(Aux.INTENT_PASSWORD);

        passdata = new PasswordData();
        passdata.parseString(data);

        // Button dimensions
        int btnheight = (int)(Aux.SCREEN_HEIGHT*0.1);
        int btnwidth = (int)(Aux.SCREEN_WIDTH*0.33);
        int listheight = (int)(Aux.SCREEN_HEIGHT*0.2);

        // Creating the list of entries
        namelist = new ListView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listheight);
        params.setMargins(0,Aux.MARGIN_TOP,0,0);
        namelist.setLayoutParams(params);

        // Creating the title for the app
        TextView title = Aux.AppTitle(this);

        // Creating the list title
        TextView listtitle = new TextView(this);
        listtitle.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
        listtitle.setTextColor(Color.GREEN);
        listtitle.setTextSize(prefs.get(CCryptPreferences.FONT_SIZE));
        listtitle.setText("Select an entry to edit");
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,Aux.MARGIN_TOP,0,0);
        listtitle.setLayoutParams(params);

        // Putting it all together
        LinearLayout global = new LinearLayout(this);
        global.setOrientation(LinearLayout.VERTICAL);
        global.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        global.setBackgroundColor(Color.BLACK);

        global.addView(title);
        global.addView(listtitle);
        global.addView(namelist);

        // Adding the edit fields
        fields = new ArrayList<TextEditField>();
        for (int i = 0; i < fieldLabels.size(); i++){
            TextEditField tef = new TextEditField(this,fieldLabels.get(i));
            tef.setFontSize(prefs.get(CCryptPreferences.FONT_SIZE));
            tef.setTextColor(Color.GREEN);
            tef.setFieldBackgroudColor(Aux.EDIT_BACKGROUND_COLOR);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Aux.MARGIN_TOP, 0, 0);
            tef.setLayoutParams(params);

            fields.add(tef);
            global.addView(tef);
        }

        // Adding the buttons
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Aux.MARGIN_TOP, 0, 0);
        buttons.setLayoutParams(params);

        CustomButton addbtn = Aux.makeAppButton(this,this,"ADD",btnwidth,btnheight,Aux.BUTTON_ID_ADD);
        CustomButton cancelbtn = Aux.makeAppButton(this, this, "CANCEL", btnwidth, btnheight, Aux.BUTTON_ID_CANCELEDIT);
        CustomButton delbtn = Aux.makeAppButton(this, this, "DELETE", btnwidth, btnheight, Aux.BUTTON_ID_DELENTRY);
        CustomButton newpass = Aux.makeAppButton(this,this,"GEN PASS",(btnwidth*3)/2,btnheight,Aux.BUTTON_ID_GENERATENEWPASSWD);
        CustomButton savebtn = Aux.makeAppButton(this,this,"SAVE",(btnwidth*3)/2,btnheight,Aux.BUTTON_ID_SAVEDATA);

        buttons.addView(addbtn);
        buttons.addView(delbtn);
        buttons.addView(cancelbtn);

        LinearLayout buttons2 = new LinearLayout(this);
        buttons2.setOrientation(LinearLayout.HORIZONTAL);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttons2.setLayoutParams(params);

        buttons2.addView(newpass);
        buttons2.addView(savebtn);

        global.addView(buttons);
        global.addView(buttons2);

        setContentView(global);

        // Prevent the screen from turning off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Creating the generate password dialog
        generator = new GenerateNewPasswordDialog(this,this,prefs.get(CCryptPreferences.FONT_SIZE));

        // Default selection is -1
        selection = -1;

        // Populating the list of items
        updateList();

    }

    //============================= FILLED FIELDS WITH SELECTED DATA ===============================
    // Function that fills the data fields.
    private void fillFields(int id){
        selection = id;
        if (id == -1){
            // Fields are cleared.
            for (int i = 0; i < fields.size(); i++){
                fields.get(i).clear();
            }
        }
        else{
            // Fields should be loaded.
            fields.get(FIELD_NAME).setText(passdata.getName(id));
            fields.get(FIELD_USER).setText(passdata.getUser(id));
            fields.get(FIELD_PASS).setText(passdata.getPassword(id));
        }
    }

    // Reconstructs adapter when entry is deleted.
    private void updateList(){

        List<String> labels = new ArrayList<String>();
        labels.add("SELECT FOR NEW ENTRY");
        passdata.resetIterator();
        boolean done = false;
        while (!done){
            String name = passdata.nextName();
            if (name == null){
                done = true;
            }
            else {
                labels.add(name);
            }
        }

        int spinner_font_size = prefs.get(CCryptPreferences.FONT_SIZE)*3;
        final Aux.CustomAdapter adapter = new Aux.CustomAdapter(this,
                android.R.layout.simple_spinner_item,
                Typeface.MONOSPACE,
                Aux.BUTTON_FONT_COLOR,
                spinner_font_size,
                labels);
        adapter.setBackgroundColor(Color.BLACK);
        adapter.setSelectedColor(Color.RED);
        namelist.setAdapter(adapter);
        namelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPos(position);
                fillFields(position-1);
            }
        });
        fillFields(-1);
    }

    //============================= SELECTING WHAT TO DO WHEN A BUTTON IS PRESSED ===============================
    @Override
    public void buttonSwiped(int id, boolean totheright) {

    }

    @Override
    public void buttonPressed(int id) {

        switch (id){
            case Aux.BUTTON_ID_GENERATENEWPASSWD:
                generator.show();
                break;
            case Aux.BUTTON_ID_USEGENPASSWD:
                fields.get(FIELD_PASS).setText(generator.getPassword());
                generator.dismiss();
                break;
            case Aux.BUTTON_ID_ADD:
                // Field name must not be empty.
                if (fields.get(FIELD_NAME).getFieldText().isEmpty()){
                    showError("The name cannot be empty");
                    return;
                }

                // Generating the data to be saved.
                String error = "";
                if (selection == -1) {
                    // Must create new entry, if name is not ALL fields are empty
                    if (!fields.get(FIELD_PASS).getFieldText().isEmpty()) {
                        error = passdata.insertData(fields.get(FIELD_NAME).getFieldText(),
                                fields.get(FIELD_USER).getFieldText(),
                                fields.get(FIELD_PASS).getFieldText());
                    }
                    else{
                        error = "Cannot have empty password field";
                    }
                }
                else {
                    // Must edit existing entry.
                    error = passdata.modifyData(selection,
                            fields.get(FIELD_NAME).getFieldText(),
                            fields.get(FIELD_USER).getFieldText(),
                            fields.get(FIELD_PASS).getFieldText());
                }

                if (!error.isEmpty()) {
                    showError("Error inserting data: " + error);
                    return;
                }
                updateList();
                break;

            case Aux.BUTTON_ID_SAVEDATA:
                encryptData();
                break;
            case Aux.BUTTON_ID_DELENTRY:
                passdata.deleteDataEntry(selection);
                updateList();
                break;
            case Aux.BUTTON_ID_CANCELEDIT:
                Intent intent;
                intent = new Intent(this, ShowDataView.class);
                intent.putExtra(Aux.INTENT_CODED_STRING, bkpdata);
                intent.putExtra(Aux.INTENT_PASSWORD,epassword);
                startActivity(intent);
                finish();
            break;
        }

    }


    public void encryptData(){

        AESCrypt engine = new AESCrypt(epassword,Aux.DATA_FILE);

        if (!engine.getStatus().isEmpty()){
            showError("Could not initialize AES Engine: " + engine.getStatus());
            return;
        }

        engine.encrypt(passdata.generateDataString());

        if (!engine.getStatus().isEmpty()){
            showError("Could not encrypt data: " + engine.getStatus());
            return;
        }
        else{
            Intent intent;
            intent = new Intent(this, ShowDataView.class);
            intent.putExtra(Aux.INTENT_CODED_STRING, passdata.generateDataString());
            intent.putExtra(Aux.INTENT_PASSWORD,epassword);
            startActivity(intent);
            finish();
        }
    }

    public void showError(String msg){
        Toast t = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        t.show();
    }

    //========================= DIALOG CLASSES ==============================
//    private static class AskPasswordDialog extends AlertDialog{
//
//        private EditText ePasswordField;
//        private TextView instructionField;
//        private boolean ToDelete;
//
//        protected AskPasswordDialog(Context context, CustomButtonPressedInterface cbi, int fontsize) {
//            super(context);
//            // Drawing the dialog.
//
//            // Password field
//            ePasswordField = new EditText(context);
//            ePasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            ePasswordField.setTextColor(Color.GREEN);
//            ePasswordField.setBackgroundColor(Color.DKGRAY);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(10,0,10,0);
//            ePasswordField.setLayoutParams(params);
//
//            // Creating the list title
//            instructionField = new TextView(context);
//            instructionField.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
//            instructionField.setTextColor(Color.GREEN);
//            instructionField.setTextSize(fontsize);
//            instructionField.setText("My Dialog");
//            instructionField.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
//            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(10, 0, 10, 0);
//            instructionField.setLayoutParams(params);
//
//            int btnh = (int)(Aux.SCREEN_HEIGHT*0.08);
//            int btnw = (int)(Aux.SCREEN_WIDTH*0.45);
//
//            // Creating the title
//            TextView title = new TextView(context);
//            title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
//            title.setTextColor(Color.GREEN);
//            title.setText("PASSWORD");
//            title.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
//            title.setGravity(Gravity.CENTER);
//            title.setTextSize(fontsize);
//            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            title.setLayoutParams(params);
//            setCustomTitle(title);
//
//            // Global layout.
//            LinearLayout ll = new LinearLayout(context);
//            ll.setOrientation(LinearLayout.VERTICAL);
//            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            ll.setLayoutParams(params);
//            ll.addView(instructionField);
//            ll.addView(ePasswordField);
//            ll.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
//
//            // Button layout
//            CustomButton ok = Aux.makeAppButton(context,cbi,"OK",btnw,btnh,Aux.BUTTON_ID_OKPASSWD);
//            CustomButton cancel = Aux.makeAppButton(context,cbi,"CANCEL",btnw,btnh,Aux.BUTTON_ID_CANCELEDIT);
//            LinearLayout buttons = new LinearLayout(context);
//            buttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            buttons.addView(ok);
//            buttons.addView(cancel);
//
//            // Adding button layout
//            ll.addView(buttons);
//
//            setView(ll);
//
//            ToDelete = false;
//        }
//
//        public void setInstruction(String instruction){
//            instructionField.setText(instruction);
//        }
//
//        public void clearPassword(){
//            ePasswordField.setText("");
//        }
//
//        public void setIntentionToDelete(boolean todelete){ToDelete = todelete;}
//
//        public boolean intentIsToDelete(){return ToDelete;}
//
//        public String getPassword(){
//            return ePasswordField.getText().toString();
//        }
//    }

    private static class GenerateNewPasswordDialog extends AlertDialog implements CustomButtonPressedInterface{

        private TextView passwordFormat;
        private TextView genPassword;

        // Buttons

        private static final int BTN_UPPER = 0;
        private static final int BTN_LOWER = 1;
        private static final int BTN_NUMBER = 2;
        private static final int BTN_SYMBOL = 3;
        private static final int BTN_BKPSPACE = 4;
        private static final int BTN_GENERATE = 5;

        // Genearator
        PasswordGenerator pgen;

        protected GenerateNewPasswordDialog(Context context, CustomButtonPressedInterface cbi, int fontsize){
            super(context);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setLayoutParams(params);

            // Password generator
            pgen = new PasswordGenerator("");

            passwordFormat = new TextView(context);
            passwordFormat.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            passwordFormat.setTextColor(Color.GREEN);
            passwordFormat.setTextSize(fontsize);
            passwordFormat.setText(pgen.getFormat());
            passwordFormat.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            passwordFormat.setLayoutParams(params);

            genPassword = new TextView(context);
            genPassword.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            genPassword.setTextColor(Color.GREEN);
            genPassword.setTextSize(fontsize);
            genPassword.setText("");
            genPassword.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            genPassword.setLayoutParams(params);

            TextView indications = new TextView(context);
            indications.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            indications.setTextColor(Color.GREEN);
            indications.setTextSize(fontsize);
            indications.setText("Generate a formatted random password\nThis field indicates the password format to be generated: \n  A: Is an uppercase letter \n  a: Is a lowercase letter \n  0: Is number \n  s: Is a symbol \n  <: Backspace");
            indications.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            indications.setLayoutParams(params);

            TextView namedbuffer = new TextView(context);
            namedbuffer.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            namedbuffer.setTextColor(Color.GREEN);
            namedbuffer.setTextSize(fontsize);
            namedbuffer.setText("Generated password");
            namedbuffer.setBackgroundColor(Aux.EDIT_BACKGROUND_COLOR);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            namedbuffer.setLayoutParams(params);

            int btnh = (int)(Aux.SCREEN_HEIGHT*0.08);
            int btnw = (int)(Aux.SCREEN_WIDTH*0.18);
            int butw = (int)(Aux.SCREEN_WIDTH*0.4);

            TextView buffer = new TextView(context);
            buffer.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            buffer.setTextColor(Color.GREEN);
            buffer.setTextSize(fontsize);
            buffer.setText("Password format");
            buffer.setBackgroundColor(Aux.EDIT_BACKGROUND_COLOR);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            buffer.setLayoutParams(params);

            CustomButton upper = Aux.makeAppButton(context,this,"A",btnw,btnh,BTN_UPPER);
            CustomButton lower = Aux.makeAppButton(context,this,"a",btnw,btnh,BTN_LOWER);
            CustomButton symbol = Aux.makeAppButton(context,this,"s",btnw,btnh,BTN_SYMBOL);
            CustomButton number = Aux.makeAppButton(context,this,"0",btnw,btnh,BTN_NUMBER);
            CustomButton bkpspace = Aux.makeAppButton(context,this,"<",btnw,btnh,BTN_BKPSPACE);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout cmds = new LinearLayout(context);
            cmds.setOrientation(LinearLayout.HORIZONTAL);
            cmds.setLayoutParams(params);

            cmds.addView(lower);
            cmds.addView(upper);
            cmds.addView(symbol);
            cmds.addView(number);
            cmds.addView(bkpspace);

            CustomButton generate = Aux.makeAppButton(context,this,"GENERATE",butw,btnh,BTN_GENERATE);
            CustomButton use = Aux.makeAppButton(context,cbi,"USE",butw,btnh,Aux.BUTTON_ID_USEGENPASSWD);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout buttons = new LinearLayout(context);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setLayoutParams(params);

            buttons.addView(generate);
            buttons.addView(use);

            ll.addView(indications);
            ll.addView(buffer);
            ll.addView(passwordFormat);
            ll.addView(cmds);
            ll.addView(namedbuffer);
            ll.addView(genPassword);
            ll.addView(buttons);
            ll.setBackgroundColor(Aux.DIALOG_BACKGROUND_COLOR);

            setView(ll);

        }

        public String getPassword() {return genPassword.getText().toString(); }

        @Override
        public void buttonPressed(int id) {
            String data = passwordFormat.getText().toString();
            String add = "";
            switch (id){
                case BTN_LOWER:
                    add = "a";
                    break;
                case BTN_NUMBER:
                    add = "0";
                    break;
                case BTN_SYMBOL:
                    add = "s";
                    break;
                case BTN_UPPER:
                    add = "A";
                    break;
                case BTN_GENERATE:
                    pgen.setFormat(passwordFormat.getText().toString());
                    genPassword.setText(pgen.newPassword());
                    return;
            }
            if (add.isEmpty()) {
               // Need to delete last char.
               data = data.substring(0,data.length()-1);
            }
            else{
                data = data + add;
            }
            passwordFormat.setText(data);
        }

        @Override
        public void buttonSwiped(int id, boolean totheright) {

        }
    }


    // To disable the back button
    @Override
    public void onBackPressed() {
    }


}
