package com.vieira.joao;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;

import static com.vieira.joao.AuxFunctions.*;
import static com.vieira.joao.AuxFunctions.addSecurity;

public class Protect {

    private static final Logger logger = LoggerFactory.getLogger(Protect.class);

    public static void main(String[] args) throws Exception {

        // Check arguments
        if (args.length < 4) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java Protect inputJSONfile outputJSONfile clientPublicKey serverPrivateKey");
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
            logger.info("Voucher does not exist");
            AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);
            String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);
            JsonObject digitalSignature = AuxFunctions.createJsonDigitalSignatureHash(hash, serverPrivateKey);
            AuxFunctions.addSecurity(outputJSONname, digitalSignature);
            System.exit(0);
        }

        //create copy of input json
        AuxFunctions.copyFileUsingStream(inputJSONname,outputJSONname);

        //encrypt fields of voucher on new json
        AuxFunctions.encryptVoucherPublicKey(outputJSONname, clientPublicKey);

        //create cryptographic hash
        String hash = AuxFunctions.getRestaurantInfoHash(outputJSONname);

        //create nonce and add to file
        String nonce = AuxFunctions.createAndSaveNonce(nonceFile);

        //create digital signature JSON
        JsonObject digitalSignature = AuxFunctions.createJSONDigitalSignature(hash, nonce, serverPrivateKey);

        //add digest to json
        AuxFunctions.addSecurity(outputJSONname, digitalSignature);
    }

    public static void protectVouchers(String jsonName, String jsonName2, String publicKeyPath) throws Exception {

        PublicKey clientPublicKey = getPublicKey(publicKeyPath);
        PrivateKey serverPrivateKey = getPrivateKey("keys/serverPrivate.key");

        copyFileUsingStream(jsonName, jsonName2);

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

            // TODO: Remove base64 encoding before encryption
            byte[] encryptedHashBytes = newCipher.doFinal(mealVouchersHash.getBytes());
            String encryptedB64Hash = Base64.getEncoder().encodeToString(encryptedHashBytes);

            JsonObject digitalSignature = new JsonObject();
            digitalSignature.addProperty("hash", encryptedB64Hash);

            addSecurity(jsonName2, digitalSignature);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public static void protectFind(String inputJSONname, String outputJSONname, String clientPublicKeyName, String serverPrivateKeyName) throws Exception{
        //get client public and server private keys
        PrivateKey serverPrivateKey = getPrivateKey(serverPrivateKeyName);
        PublicKey clientPublicKey = getPublicKey(clientPublicKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = voucherExists(inputJSONname);

        if (!voucherexists) {
            copyFileUsingStream(inputJSONname, outputJSONname);
            createTimestamp(outputJSONname,"restaurantInfo");
            String hash = getRestaurantInfoHash(outputJSONname);
            JsonObject digitalSignature = createJsonDigitalSignatureHash(hash, serverPrivateKey);
            addSecurity(outputJSONname, digitalSignature);
            return;
        }

        //create copy of input json
        copyFileUsingStream(inputJSONname, outputJSONname);

        //create timestamp
        createTimestamp(outputJSONname,"restaurantInfo");

        //encrypt fields of voucher on new json
        encryptVoucherPublicKey(outputJSONname, clientPublicKey);

        //create cryptographic hash
        String hash = getRestaurantInfoHash(outputJSONname);

        //create digital signature JSON
        JsonObject digitalSignature = createJsonDigitalSignatureHash(hash, serverPrivateKey);

        //add digest to json
        addSecurity(outputJSONname, digitalSignature);
    }
}