package com.colocrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ariel on 7/23/15.
 */
public class PasswordData {

    private ArrayList<DataStruct> data;
    private int iterator;
    private static final String FIELD_SEPARATOR = "|";
    private static final String FIELD_REGEX = "\\" + FIELD_SEPARATOR;
    private static final String TRIPLET_SEPARATOR = "<>";

    // Table variables
    private int col_name_width;
    private int col_user_width;
    private int col_pass_width;

    private class DataStruct{

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name.trim();
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user.trim();
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password){
            this.password = password.trim();
        }

        public String name;
        public String user;
        public String password;

        public DataStruct(String thename, String theuser, String passwd){
            name = thename.trim();
            user = theuser.trim();
            password = passwd.trim();
        }

        public String toString(String separator){
            return name + separator + user + separator + password;
        }

    }

    public PasswordData(){
        data = new ArrayList<DataStruct>();
        col_name_width = 0;
        col_pass_width = 0;
        col_user_width = 0;
    }

    //======================= MODIFY THE DATA =====================

    public String insertData(String name, String user, String passwd){
        if (name.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (user.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (passwd.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (name.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        if (user.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        if (passwd.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        data.add(new DataStruct(name,user,passwd));
        return "";
    }

    public String modifyData(int id, String name, String user, String passwd){
        if (name.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (user.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (passwd.indexOf(FIELD_SEPARATOR) != -1) return "No field can contain the character | ";
        if (name.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        if (user.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        if (passwd.indexOf(TRIPLET_SEPARATOR) != -1) return "No field can contain the sequence <> ";
        if ((id > -1) && (id < data.size())){
            data.get(id).setName(name);
            data.get(id).setUser(user);
            data.get(id).setPassword(passwd);
            return "";
        }
        else return "Invalid id for data: " + id;
    }

    public String deleteDataEntry(int id){
        if ((id > -1) && (id < data.size())){
            data.remove(id);
            return "";
        }
        else return "Invalid id for data: " + id;
    }

    //======================= GETTING INDIVIDUAL VALUES =====================

    public void resetIterator(){
        iterator = 0;
    }

    public String nextName(){
        if (iterator < data.size()){
            String ret = data.get(iterator).getName();
            iterator++;
            return ret;
        }
        else return null;
    }

    public String getName(int id){
        if ((id > -1) && (id < data.size())){
            return data.get(id).getName();
        }
        else return "";
    }

    public String getPassword(int id){
        if ((id > -1) && (id < data.size())){
            return data.get(id).getPassword();
        }
        else return "";
    }

    public String getUser(int id){
        if ((id > -1) && (id < data.size())){
            return data.get(id).getUser();
        }
        else return "";
    }

    //======================= CREATING FROM AND PARSING TO STRING =====================

    public String generateDataString(){
        String ret = "";
        for (int i = 0; i < data.size()-1; i++){
            ret = ret + data.get(i).toString(FIELD_SEPARATOR) + TRIPLET_SEPARATOR;
        }
        if (data.size() > 0) ret = ret + data.get(data.size()-1).toString(FIELD_SEPARATOR);
        return ret;
    }

    public boolean parseString(String sdata){

        if (sdata == null){
            return true;
        }

        // This happens if there is no data.
        if (sdata.isEmpty()){
            return true;
        }

        // Double field separators should separate triplets
        String[] triplets = sdata.split(TRIPLET_SEPARATOR);

        for (int i = 0; i < triplets.length; i++){
            String[] triplet = triplets[i].split(FIELD_REGEX);

            /*String temp = "";
            for (int j = 0; j < triplet.length; j++){
                temp = temp + Integer.toString(j) + ": " + triplet[j];
            }

            System.err.println("Will separate " + triplets[i] + " into " + temp);*/

            if (triplet.length != 3) return false;
            data.add(new DataStruct(triplet[0],triplet[1],triplet[2]));
        }

        return true;

    }

    //================= FUNCTIONS FOR RETURNING THE DATA ===================
    public List<String> getItemList(){
        List<String> items = new ArrayList<>();

        for (int i = 0; i < data.size(); i++){
            items.add(data.get(i).getName());
        }

        return items;
    }


    //======================= TABLE FORMATTING THE DATA =====================
    public String tableFormatData(){

        if (data.size() < 1){
            return "No data to show";
        }

        String nameCol = "Name";
        String userCol = "Username";
        String passCol = "Password";

        int col_pass_width = passCol.length();
        int col_user_width = userCol.length();
        int col_name_width = nameCol.length();

        // First pass for measurements
        for (int i = 0; i < data.size(); i++) {
            col_name_width = Math.max(col_name_width, data.get(i).getName().length());
            col_pass_width = Math.max(col_pass_width, data.get(i).getPassword().length());
            col_user_width = Math.max(col_user_width, data.get(i).getUser().length());
        }

        // Each column is added the number of spaces at each side
        col_user_width = col_user_width + 2;
        col_pass_width = col_pass_width + 2;
        col_name_width = col_name_width + 2;

        // 1 space at the begginning, two spaces (one at each side) of eacch column (2*3), two spaces for the column separation
        int TableWidth = col_name_width + col_pass_width + col_user_width + 3;

        // Divisor for row names
        String divisor = " ";
        for (int i = 0; i < TableWidth-1; i++){
            divisor = divisor + "=";
        }

        // Table name
        String table = " " + normString(nameCol,col_name_width) + "|"
                + normString(userCol,col_user_width) + "|"
                + normString(passCol,col_pass_width) + "\n";

        table = table + divisor + "\n";

        // Space between rows
        String rowsep = " ";
        for (int i = 0; i < col_name_width; i++) {
            rowsep = rowsep + " ";
        }
        rowsep = rowsep + "|";
        for (int i = 0; i < col_user_width; i++) {
            rowsep = rowsep + " ";
        }
        rowsep = rowsep + "|";
        for (int i = 0; i < col_pass_width; i++) {
            rowsep = rowsep + " ";
        }
        rowsep = rowsep + "\n";

        for (int i = 0; i < data.size(); i++) {
           table = table + " " + normString(data.get(i).getName(),col_name_width) + "|"
                    + normString(data.get(i).getUser(),col_user_width) + "|"
                    + normString(data.get(i).getPassword(),col_pass_width) + "\n" + rowsep;
        }

        return table;


    }

    private String normString(String s, int length){
        int rem = length - s.length();
        int sleft = rem/2;
        int sright = rem - sleft;

        String left = "";
        for (int i = 0; i < sleft; i++){
            left = left + " ";
        }

        String right = "";
        for (int i = 0; i < sright; i++){
            right = right + " ";
        }

        return left + s + right;

    }


}
