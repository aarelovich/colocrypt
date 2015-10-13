package com.colocrypt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * All the necessary functions to encrypt and decrypt data.
 */
public class AESCrypt {

    // Constants
    private final static String encoding = "UTF-8";
    private final static String algorithm = "AES/CBC/PKCS5Padding";
    private final static int NCHARS = 16;

    // Private variables
    private byte[] rawdata;
    private byte[] keyBytes;
    private String status;
    private String fileName;

    // AES Variables
    private static Cipher cipher;
    private static SecretKeySpec secretKeySpec;
    private static IvParameterSpec ivParameterSpec;

    //====================== AES Engine intialization ======================
    public AESCrypt (String passwd,String datafile){

        // Initializing variables.
        rawdata = null;
        status = "";
        fileName = datafile;

        // Transforming the passwd to 16 bytes.
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            InputStream in = new ByteArrayInputStream(Charset.forName(encoding).encode(passwd).array());
            byte[] buffer = new byte[NCHARS];
            int byteCount;
            while ((byteCount = in.read(buffer)) > 0) {
                digester.update(buffer, 0, byteCount);
            }
            keyBytes = digester.digest();
        }
        catch(Exception e){
            status = "Error in key generation: " + e.toString();
        }

        // Initilizing the crypto engine
        try {
            cipher = Cipher.getInstance(algorithm);
        }
        catch(Exception e){
            status = "Error in intialization: " + e.toString();
        }
        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        ivParameterSpec = new IvParameterSpec(keyBytes);

    }

    //====================== AES Encryption ======================
    public void encrypt(String plaintext){
        status = "";
        try{
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            rawdata = cipher.doFinal(plaintext.getBytes("UTF8"));
        }
        catch(Exception e){
            status = "Error encrypting: " + e.toString();
        }

        if (!status.isEmpty()) return;

        // Writing the file with encrypted data.
        try{
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(fileName));

            // This way the first four bytes represent the length of the written data.
            fos.writeInt((int)rawdata.length);
            fos.write(rawdata,0,rawdata.length);
            fos.close();

        }
        catch(Exception e){
            status = "Error writing the file: " + e.toString();
        }
    }

    //====================== AES Decryption ======================
    public String decrypt(){
        status = "";
        String data = "";

        // Opening the file for reading
        byte[] eBytes = null;
        try{
            DataInputStream fis = new DataInputStream(new FileInputStream(fileName));

            // Reading the size of the written files
            int size = fis.readInt();

            // Reading the rest of the file
            eBytes = new byte[size];
            fis.readFully(eBytes);
            fis.close();

        }
        catch(Exception e){
            status = "Error reading the file: " + e.toString();
        }

        if (!status.isEmpty()) return data;

        // The data is read and it is now decrypted.
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            rawdata = cipher.doFinal(eBytes);
            data = new String(rawdata,encoding);
        }
        catch(Exception e){
            status = "Error decrypting the file: " + e.toString();
        }

        return data;
    }

    //====================== Other functions ======================

    // Getting status
    public String getStatus(){return status;}

}
