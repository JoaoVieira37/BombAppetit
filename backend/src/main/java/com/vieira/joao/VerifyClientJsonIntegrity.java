package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

public class VerifyClientJsonIntegrity {

    public static boolean verify(String JSONname, String publicKeyName) throws Exception {
        //get client public key

        PublicKey publicKey = AuxFunctions.getPublicKey(publicKeyName);

        //decrypt hash
        String hash = AuxFunctions.decryptHashFromJson(JSONname, publicKey);

        //check hash value
        boolean isHash = AuxFunctions.isClientHashValid(hash, JSONname, "info");

        //check timestamp
        boolean isTime = AuxFunctions.isClientTimestampValid(JSONname, "info");

        return isTime && isHash;
    }

    public static boolean verify2(JsonObject json, String publicKeyName) throws Exception {

        //get client public key
        PublicKey publicKey = AuxFunctions.getPublicKey(publicKeyName);

        //decrypt hash
        String hash = decryptHashFromJson(json, publicKey);

        // TODO: Remove the "info" argument (After fixing the requestBody structure)
        return isClientHashValid(hash, json, "info") && isClientTimestampValid(json, "info");
    }

    public static String decryptHashFromJson(JsonObject json, PublicKey key) {

        JsonObject securityObject = (JsonObject) json.get("security");
        String encryptedHash = securityObject.get("hash").getAsString();

        try {

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decodes from base64 to bytes the deciphers the bytes
            byte[] unencryptedHashBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedHash.getBytes()));

            // TODO: Fix this second decoding in the library code
            byte[] unencryptedHash = Base64.getDecoder().decode(unencryptedHashBytes);

            return Base64.getEncoder().encodeToString(unencryptedHash);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        return null;
    }

    public static boolean isClientHashValid(String hash, JsonObject json, String field) throws Exception {

        final String DIGEST_ALGO = "SHA-256";

        Debug.debug(json.toString());

        JsonObject object = json.get(field).getAsJsonObject();
        byte[] bytes = object.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();

        Debug.debug(Arrays.toString(digestBytes));
        Debug.debug("Client Digest: " + Base64.getEncoder().encodeToString(digestBytes));

        return Base64.getEncoder().encodeToString(digestBytes).equals(hash);

    }

    public static boolean isClientTimestampValid(JsonObject json, String field) {

        String timestamp = json.get(field).getAsJsonObject().get("timestamp").getAsString();

        long timeCurrent = new Date().getTime();
        long timestampLong = Long.parseLong(timestamp);

        //2 minutes
        long buffer = 120000;

        long upperlimit = timeCurrent + buffer;
        long lowerlimit = timeCurrent - buffer;

        return lowerlimit <= timestampLong && timestampLong <= upperlimit;

    }

}