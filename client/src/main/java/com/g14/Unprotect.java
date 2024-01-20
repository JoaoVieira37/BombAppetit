package com.g14;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import java.nio.file.Path; 
import java.nio.file.Paths;

public class Unprotect {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 5) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java Unprotect inputJSONfile outputJSONfile clientPrivateKey serverPublicKey type");
            return;
        }

        final String inputJSONname = args[0];
        final String outputJSONname = args[1];
        final String clientPrivateKeyName = args[2];
        final String serverPublicKeyName = args[3];
        final String type = args[4];

        if (type.equals("voucher")) {
            //get client private key
            PrivateKey clientPrivateKey = AuxFunctions.getPrivateKey(clientPrivateKeyName);

            //get server public key
            PublicKey serverPublicKey = AuxFunctions.getPublicKey(serverPublicKeyName);

            AuxFunctions.copyFileUsingStream(inputJSONname,outputJSONname);

            String hash = AuxFunctions.decryptHashFromJson(outputJSONname, serverPublicKey);

            boolean isHash = AuxFunctions.isClientHashValid(hash, outputJSONname, "mealVouchers");
            boolean isTime = AuxFunctions.isClientTimestampValid(outputJSONname, "mealVouchers");

            if (!isTime || !isHash){
                System.out.println("Hash or timestamp of response are wrong!");
                //System.exit(0);
            }

            FileReader fileReader = new FileReader(outputJSONname);
            Gson gson = new Gson();
            JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
            JsonObject mealVoucherObject = rootJson.get("mealVouchers").getAsJsonObject();
            mealVoucherObject.remove("timestamp");

            JsonArray voucherList = mealVoucherObject.getAsJsonArray("list");
            for (JsonElement element : voucherList) {
                JsonObject object = element.getAsJsonObject();

                String encryptedCode = object.get("code").getAsString();
                String encryptedId = object.get("id").getAsString();

                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, clientPrivateKey);

                byte[] unencryptedCodeBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCode.getBytes()));
                String unencryptedCode = new String(unencryptedCodeBytes);

                byte[] unencryptedIdBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedId.getBytes()));
                String unencryptedId = new String(unencryptedIdBytes);

                object.addProperty("id", unencryptedId);
                object.addProperty("code", unencryptedCode);
            }

            try (FileWriter fileWriter = new FileWriter(outputJSONname)) {
                Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                gson2.toJson(rootJson, fileWriter);
            }

            AuxFunctions.removeSecurity(outputJSONname);

            return;
        }


        //get client private key
        PrivateKey clientPrivateKey = AuxFunctions.getPrivateKey(clientPrivateKeyName);

        //get server public key
        PublicKey serverPublicKey = AuxFunctions.getPublicKey(serverPublicKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = AuxFunctions.voucherExists(inputJSONname);

        //create copy of input json
        AuxFunctions.copyFileUsingStream(inputJSONname,outputJSONname);

        //decrypt hash
        String hash = AuxFunctions.decryptHashFromJson(outputJSONname, serverPublicKey);

        //check if hash is valid
        boolean isHash = AuxFunctions.isClientHashValid(hash, outputJSONname, "restaurantInfo");

        //check if timestamp is valid
        boolean isTime = AuxFunctions.isClientTimestampValid(outputJSONname, "restaurantInfo");

        if (!isTime || !isHash){
            System.out.println("Hash or timestamp of response are wrong!");
            System.exit(0);
        }

        if(voucherexists){
            AuxFunctions.decryptVoucherPrivateKey(outputJSONname, clientPrivateKey);
        }

        //remove security (digital signature)
        AuxFunctions.removeSecurity(outputJSONname);
    }
}