package com.vieira.joao;

import com.google.gson.JsonObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyClientJsonIntegrity {

    private static final Logger logger = LoggerFactory.getLogger(VerifyClientJsonIntegrity.class);

    public static boolean verify(JsonObject json, String publicKeyPath) throws Exception {

        //get client public key
        PublicKey publicKey = AuxFunctions.getPublicKey(publicKeyPath);

        //decrypt hash
        byte[] hash = decryptHashFromJson(json, publicKey);

        // TODO: Remove the "info" argument (After fixing the requestBody structure)
        return isClientHashValid(hash, json, "info") && isClientTimestampValid(json, "info");
    }

    private static byte[] decryptHashFromJson(JsonObject json, PublicKey key) {

        JsonObject securityObject = json.get("security").getAsJsonObject();
        String encryptedHash = securityObject.get("hash").getAsString();

        try {

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decodes from base64 to bytes then deciphers the bytes
            byte[] unencryptedHashBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedHash.getBytes()));

            // TODO: Fix this second decoding in the library code
            // byte[] unencryptedHash = Base64.getDecoder().decode(unencryptedHashBytes);

            return unencryptedHashBytes;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private static boolean isClientHashValid(byte[] hash, JsonObject json, String field) throws Exception {

        final String DIGEST_ALGO = "SHA-256";

        logger.debug(json.toString());

        JsonObject object = json.get(field).getAsJsonObject();
        byte[] bytes = object.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();

        logger.debug(Arrays.toString(digestBytes));
        logger.debug("Client Digest: " + Base64.getEncoder().encodeToString(digestBytes));

        return Arrays.equals(digestBytes, hash);

    }

    private static boolean isClientTimestampValid(JsonObject json, String field) {

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