package com.colocrypt;

import java.util.Random;

/**
 * Created by ariela on 10/2/15.
 */
public class PasswordGenerator {

    // Databases
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String SYMBOLS = "!#%&=¿?¡+-";

    // Default format
    private static final String DEFAULT_FORMAT = "Aaaas0000s";

    // Password format
    private String pformat = "";

    // Constructor
    public PasswordGenerator(String format){
        if (format.isEmpty()){
            pformat = DEFAULT_FORMAT;
        }
        else {
            pformat = format;
        }
    }

    // Getting the format
    public String getFormat(){return pformat;}

    // Setting the format
    public void setFormat(String format){ pformat = format;}

    // Getting a new password
    public String newPassword(){

        String password = "";

        Random random = new Random();

        for (int i = 0; i < pformat.length(); i++){
            if (pformat.charAt(i) == 'A'){
                password = password + UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
            }
            else if (pformat.charAt(i) == 'a'){
                password = password + LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
            }
            else if (pformat.charAt(i) == 's'){
                password = password + SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
            }
            else if (pformat.charAt(i) == '0'){
                password = password + Integer.toString(random.nextInt(10));
            }
        }
        return password;
    }


}
