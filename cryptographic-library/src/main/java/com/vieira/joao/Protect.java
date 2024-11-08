package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import static com.vieira.joao.AuxFunctions.*;

public class Protect {

    private static final Logger logger = LoggerFactory.getLogger(Protect.class);

    public static void main(String[] args) throws Exception {

        // Check arguments
        if (args.length < 4) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java Protect inputJSONfile outputJSONfile clientPublicKey serverPrivateKey");
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
        AuxFunctions.copyFileUsingStream(inputJSONname, outputJSONname);

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

    public static void protectVouchers(String mealVouchersObjectString, String publicKeyPath) throws Exception {
        protectVouchers(new Gson().fromJson(mealVouchersObjectString, JsonObject.class), publicKeyPath);
    }

    public static JsonObject protectVouchers(JsonObject mealVouchersObject, String publicKeyPath) throws Exception {
        logger.trace("Entered protectVouchers...");

        PublicKey clientPublicKey = getPublicKey(publicKeyPath);
        // TODO: Use some kind of key management
        PrivateKey serverPrivateKey = getPrivateKey("keys/serverPrivate.key");

        // TODO: Maybe change this encryption to symmetric encryption
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);

        JsonArray voucherList = mealVouchersObject.getAsJsonArray("mealVouchers");
        String voucherListString = voucherList.toString();
        byte[] encryptedMealVouchers = cipher.doFinal(voucherListString.getBytes());
        String encryptedMealVouchersB64 = Base64.getEncoder().encodeToString(encryptedMealVouchers);

        mealVouchersObject.addProperty("mealVouchers", encryptedMealVouchersB64);
//        for (JsonElement voucher : voucherList) {
//            JsonObject voucherObject = voucher.getAsJsonObject();
//
//            String unencryptedCode = voucherObject.get("code").getAsString();
//            byte[] encryptedCodeBytes = cipher.doFinal(unencryptedCode.getBytes());
//            String encryptedB64Code = Base64.getEncoder().encodeToString(encryptedCodeBytes);
//
//            String unencryptedId = voucherObject.get("id").getAsString();
//            byte[] encryptedIdBytes = cipher.doFinal(unencryptedId.getBytes());
//            String encryptedB64Id = Base64.getEncoder().encodeToString(encryptedIdBytes);
//
//            voucherObject.addProperty("id", encryptedB64Id);
//            voucherObject.addProperty("code", encryptedB64Code);
//        }

        Date date = new Date();
        mealVouchersObject.addProperty("timestamp", String.valueOf(date.getTime()));

        // TODO: Refactor the hashing to an AuxFunction just for Hashing.
        final String DIGEST_ALGO = "SHA-256";

        // Calculates the digest over the Base64 encoded encrypted values
        byte[] bytes = mealVouchersObject.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] mealVouchersHash = messageDigest.digest();

        // TODO: Refactor the publicKey Cryptography to an AuxFunction just for encrypting and decrypting
        Cipher newCipher = Cipher.getInstance("RSA");
        newCipher.init(Cipher.ENCRYPT_MODE, serverPrivateKey);

        byte[] encryptedHashBytes = newCipher.doFinal(mealVouchersHash);
        String encryptedB64Hash = Base64.getEncoder().encodeToString(encryptedHashBytes);

        // JsonObject digitalSignature = new JsonObject();
        // digitalSignature.addProperty("hash", encryptedB64Hash);

        // TODO: Change this to include the security in the mealVouchers object, instead of an additional
        //  security property (maybe not feasible because other jsonObjects may need this additional security property)
        mealVouchersObject.addProperty("digital_signature", encryptedB64Hash);
        logger.debug("New secured jsonObject: {}", mealVouchersObject);

        return mealVouchersObject;
    }

    public static void protectFind(String inputJSONname, String outputJSONname, String clientPublicKeyName, String serverPrivateKeyName) throws Exception {
        //get client public and server private keys
        PrivateKey serverPrivateKey = getPrivateKey(serverPrivateKeyName);
        PublicKey clientPublicKey = getPublicKey(clientPublicKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = voucherExists(inputJSONname);

        if (!voucherexists) {
            copyFileUsingStream(inputJSONname, outputJSONname);
            createTimestamp(outputJSONname, "restaurantInfo");
            String hash = getRestaurantInfoHash(outputJSONname);
            JsonObject digitalSignature = createJsonDigitalSignatureHash(hash, serverPrivateKey);
            addSecurity(outputJSONname, digitalSignature);
            return;
        }

        //create copy of input json
        copyFileUsingStream(inputJSONname, outputJSONname);

        //create timestamp
        createTimestamp(outputJSONname, "restaurantInfo");

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