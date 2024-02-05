package com.vieira.joao;

import com.google.gson.*;

import javax.crypto.Cipher;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

public class Protect {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 4) {
            System.err.println("Argument(s) missing!");
            System.err.print("Usage: java Protect inputJSONfile outputJSONfile clientPublicKey serverPrivateKey");
            return;
        }

        final String inputJSONname = args[0];
        final String outputJSONname = args[1];
        final String clientPublicKeyName = args[2];
        final String serverPrivateKeyName = args[3];

        final String nonceFile = "nonce.txt";

        //get client public and server private keys
        PrivateKey serverPrivateKey = AuxFunctions.getPrivateKey(serverPrivateKeyName);
        PublicKey clientPublicKey = AuxFunctions.getPublicKey(clientPublicKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = AuxFunctions.voucherExists(inputJSONname);

        if (!voucherexists) {
            AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);
            String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);
            JsonObject digitalSignature = AuxFunctions.createJsonDigitalSignatureHash(hash, serverPrivateKey);
            AuxFunctions.addSecurity(outputJSONname, digitalSignature);
            System.exit(0);
        }

        //create copy of input json
        AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);

        //encrypt fields of voucher on new json
        AuxFunctions.encryptVoucherPublicKey(outputJSONname, clientPublicKey);

        //create cryptographic hash
        String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);

        //create digital signature JSON
        JsonObject digitalSignature = AuxFunctions.createJsonDigitalSignatureHash(hash, serverPrivateKey);

        //add digest to json
        AuxFunctions.addSecurity(outputJSONname, digitalSignature);
    }

    public static void protectVouchers(String jsonName, String jsonName2, String publicKeyPath) throws Exception {

        PublicKey clientPublicKey = AuxFunctions.getPublicKey(publicKeyPath);
        PrivateKey serverPrivateKey = AuxFunctions.getPrivateKey("keys/serverPrivate.key");

        AuxFunctions.copyFileUsingStream(jsonName, jsonName2);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);

        try (FileReader fileReader = new FileReader(jsonName2)) {
            Gson gson = new Gson();
            JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
            JsonObject mealVoucherObj = rootJson.getAsJsonObject("mealVouchers");
            JsonArray voucherList = mealVoucherObj.getAsJsonArray("list");
            for (JsonElement element : voucherList) {
                JsonObject object = element.getAsJsonObject();
                String unencryptedCode = object.get("code").getAsString();
                System.out.println(unencryptedCode);
                byte[] encryptedCodeBytes = cipher.doFinal(unencryptedCode.getBytes());
                String encryptedB64Code = Base64.getEncoder().encodeToString(encryptedCodeBytes);

                String unencryptedId = object.get("id").getAsString();
                byte[] encryptedIdBytes = cipher.doFinal(unencryptedId.getBytes());
                String encryptedB64Id = Base64.getEncoder().encodeToString(encryptedIdBytes);

                object.addProperty("id", encryptedB64Id);
                object.addProperty("code", encryptedB64Code);
            }

            Date date = new Date();
            mealVoucherObj.addProperty("timestamp", "" + date.getTime());

            try (FileWriter fileWriter = new FileWriter(jsonName2)) {
                Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                gson2.toJson(rootJson, fileWriter);
            }

            final String DIGEST_ALGO = "SHA-256";

            byte[] bytes = mealVoucherObj.toString().getBytes();

            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
            messageDigest.update(bytes);
            byte[] digestBytes = messageDigest.digest();
            String mealVouchersHash = Base64.getEncoder().encodeToString(digestBytes);

            Cipher newCipher = Cipher.getInstance("RSA");
            newCipher.init(Cipher.ENCRYPT_MODE, serverPrivateKey);

            byte[] encryptedHashBytes = newCipher.doFinal(mealVouchersHash.getBytes());
            String encryptedB64Hash = Base64.getEncoder().encodeToString(encryptedHashBytes);

            JsonObject digitalSignature = new JsonObject();
            digitalSignature.addProperty("hash", encryptedB64Hash);

            AuxFunctions.addSecurity(jsonName2, digitalSignature);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public static void protectFind(String inputJSONname, String outputJSONname, String clientPublicKeyName, String serverPrivateKeyName) throws Exception{
        //get client public and server private keys
        PrivateKey serverPrivateKey = AuxFunctions.getPrivateKey(serverPrivateKeyName);
        PublicKey clientPublicKey = AuxFunctions.getPublicKey(clientPublicKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = AuxFunctions.voucherExists(inputJSONname);

        if (!voucherexists) {
            AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);
            AuxFunctions.createTimestamp(outputJSONname,"restaurantInfo");
            String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);
            JsonObject digitalSignature = AuxFunctions.createJsonDigitalSignatureHash(hash, serverPrivateKey);
            AuxFunctions.addSecurity(outputJSONname, digitalSignature);
            return;
        }

        //create copy of input json
        AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);

        //create timestamp
        AuxFunctions.createTimestamp(outputJSONname,"restaurantInfo");

        //encrypt fields of voucher on new json
        AuxFunctions.encryptVoucherPublicKey(outputJSONname, clientPublicKey);

        //create cryptographic hash
        String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);

        //create digital signature JSON
        JsonObject digitalSignature = AuxFunctions.createJsonDigitalSignatureHash(hash, serverPrivateKey);

        //add digest to json
        AuxFunctions.addSecurity(outputJSONname, digitalSignature);
    }
}