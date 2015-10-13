package com.colocrypt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ariel on 7/23/15.
 */
public class CCryptPreferences {

    // Default values
    private static final List<Integer> DefaultValues = Arrays.asList(18);

    // Name for preferences.
    public static final List<String> NameForPrefs = Arrays.asList("Font size");

    public final static int FONT_SIZE = 0;

    private ArrayList<Integer> values;
    private String settings;

    private String status;

    // The constructor loads the preference file
    public CCryptPreferences(String filename){
        status = "";
        settings = filename;
        values = new ArrayList<>();
        try{
            DataInputStream dis = new DataInputStream(new FileInputStream(filename));
            for (int i = 0; i < DefaultValues.size(); i++){
                values.add(dis.readInt());
            }
            dis.close();
        }
        catch (Exception e){
            status = "Error loading settings: " + e.toString();
            for (int i = 0; i < DefaultValues.size(); i++){
                values.add(DefaultValues.get(i));
            }
        }
    }

    // ================== GETTERS ===================
    public int get(int property){
        if ((property > -1) && (property < values.size())){
            return values.get(property);
        }
        else return -1;
    }

    // ================== SETTERS ===================
    public void set(int property, int value){
        if ((property > -1) && (property < values.size())){
            values.set(property, value);
        }
    }

    //================= SAVE DATA =================
    public void save(){
        try{
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(settings));
            for (int i = 0; i < values.size(); i++){
                dos.writeInt(values.get(i));
            }
            dos.close();
        }
        catch (Exception e){
            status = "Error saving settings: " + e.toString();
        }
    }

    public String getStatus(){ return this.status; }

}
