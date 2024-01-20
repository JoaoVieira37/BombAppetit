package com.g14;

import com.google.gson.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import java.nio.file.Path; 
import java.nio.file.Paths;

import java.util.Date;

public class AuxFunctions {

    public static PublicKey getPublicKey(String clientPublicKeyName) throws Exception{
        FileInputStream pubFis = new FileInputStream(clientPublicKeyName);
        byte[] pubEncoded = new byte[pubFis.available()];
        pubFis.read(pubEncoded);
        pubFis.close();
        /* Generate public key. */
        X509EncodedKeySpec kspec = new X509EncodedKeySpec(pubEncoded);
        
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(kspec);
    }

    public static PrivateKey getPrivateKey(String serverPrivateKeyName) throws Exception{
        FileInputStream priFis = new FileInputStream(serverPrivateKeyName);
        byte[] priEncoded = new byte[priFis.available()];
        priFis.read(priEncoded);
        priFis.close();

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(priEncoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

    public static boolean voucherExists(String filename) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();

        if (restaurantInfoObject.has("mealVoucher")){ return true; }
        else{ return false; }
    }

    public static void copyFileUsingStream(String sourcename, String destname) throws IOException {
        File source = new File(sourcename);
        File dest = new File(destname);

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void encryptVoucherPublicKey(String filename, PublicKey key) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();
        JsonObject mealVoucherObject = restaurantInfoObject.get("mealVoucher").getAsJsonObject();

        String unencryptedCode = mealVoucherObject.get("code").getAsString();
        String unencryptedDescription = mealVoucherObject.get("description").getAsString();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedCodeBytes = cipher.doFinal(unencryptedCode.getBytes());
        String encryptedB64Code = Base64.getEncoder().encodeToString(encryptedCodeBytes);

        byte[] encryptedDescriptionBytes = cipher.doFinal(unencryptedDescription.getBytes());
        String encryptedB64Description = Base64.getEncoder().encodeToString(encryptedDescriptionBytes);

        mealVoucherObject.addProperty("code", encryptedB64Code);
        mealVoucherObject.addProperty("description", encryptedB64Description);

        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            gson2.toJson(rootJson, fileWriter);
        }
    }

    public static void decryptVoucherPrivateKey(String filename, PrivateKey key) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();
        JsonObject mealVoucherObject = restaurantInfoObject.get("mealVoucher").getAsJsonObject();

        String encryptedCode = mealVoucherObject.get("code").getAsString();
        String encryptedDescription = mealVoucherObject.get("description").getAsString();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] unencryptedCodeBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCode.getBytes()));
        String unencryptedCode = new String(unencryptedCodeBytes);

        byte[] unencryptedDescriptionBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedDescription.getBytes()));
        String unencryptedDescription = new String(unencryptedDescriptionBytes);

        mealVoucherObject.addProperty("code", unencryptedCode);
        mealVoucherObject.addProperty("description", unencryptedDescription);

        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            gson2.toJson(rootJson, fileWriter);
        }
    }

    public static String getRestaurantInfoHash(String filename) throws Exception{
        final String DIGEST_ALGO = "SHA-256";

        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();
        byte[] bytes = restaurantInfoObject.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();
        return Base64.getEncoder().encodeToString(digestBytes);
    }

    public static boolean isHashValid(String hash, String filename) throws Exception{
        final String DIGEST_ALGO = "SHA-256";

        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();
        byte[] bytes = restaurantInfoObject.toString().getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();
        
        return Base64.getEncoder().encodeToString(digestBytes).equals(hash);
    }

    public static String createAndSaveNonce(String filename) throws Exception{
        byte[] nonce = new byte[128];
        new SecureRandom().nextBytes(nonce);
        String nonceB64 = Base64.getEncoder().encodeToString(nonce);

        File file = new File(filename);
        FileWriter fr = new FileWriter(file, true);
        fr.write(nonceB64);
        fr.write("\n");
        fr.close();

        return nonceB64;
    }

    public static JsonObject createJSONDigitalSignature(String hash, String Nonce, PrivateKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedHashBytes = cipher.doFinal(hash.getBytes());
        String encryptedB64Hash = Base64.getEncoder().encodeToString(encryptedHashBytes);

        byte[] encryptedNonceBytes = cipher.doFinal(Nonce.getBytes());
        String encryptedB64Nonce = Base64.getEncoder().encodeToString(encryptedNonceBytes);
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hash", encryptedB64Hash);
        jsonObject.addProperty("nonce", encryptedB64Nonce);

        return jsonObject;

    }

    public static void addSecurity(String filename, JsonObject jsonObject) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        rootJson.add("security", jsonObject);

        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            gson2.toJson(rootJson, fileWriter);
        }
    }

    public static void removeSecurity(String filename) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        rootJson.remove("security");
        JsonObject restaurantInfoObject = rootJson.get("restaurantInfo").getAsJsonObject();
        // Remove unneeded timestamp
        restaurantInfoObject.remove("timestamp");

        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            gson2.toJson(rootJson, fileWriter);
        }
    }

    public static boolean isProtected(String filename) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        return rootJson.has("security");
    }

    public static List<String> decryptSecurity(String filename, PublicKey key) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject securityObject = rootJson.get("security").getAsJsonObject();

        String encryptedHash = securityObject.get("hash").getAsString();
        String encryptedNonce = securityObject.get("nonce").getAsString();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] unencryptedHashBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedHash.getBytes()));
        String unencryptedHash = new String(unencryptedHashBytes);

        byte[] unencryptedNonceBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedNonce.getBytes()));
        String unencryptedNonce = new String(unencryptedNonceBytes);

        List<String> list = new ArrayList<String>();
        list.add(0, unencryptedHash);
        list.add(1, unencryptedNonce);

        return list;
    }

    public static boolean isNonceValid(String nonce, String noncefilename) throws Exception{

        BufferedReader reader = new BufferedReader(new FileReader(noncefilename));
		String line = reader.readLine();

		while (line != null) {
			
            if (nonce.equals(line)){
                return true;
            }
            line = reader.readLine();
		}

		reader.close();

        return false;
    }

    public static String createHash(String field, String jsonfilename) throws Exception{
        final String DIGEST_ALGO = "SHA-256";

        FileReader fileReader = new FileReader(jsonfilename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject restaurantInfoObject = rootJson.get(field).getAsJsonObject();
        byte[] bytes = restaurantInfoObject.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();
        return Base64.getEncoder().encodeToString(digestBytes);
    }

    public static JsonObject createDigitalSignature(String hash, PrivateKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedHashBytes = cipher.doFinal(hash.getBytes());
        String encryptedB64Hash = Base64.getEncoder().encodeToString(encryptedHashBytes);
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hash", encryptedB64Hash);

        return jsonObject;

    }

    public static String decryptHash(String hash, PublicKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] unencryptedHashBytes = cipher.doFinal(Base64.getDecoder().decode(hash.getBytes()));
        String unencryptedHash = new String(unencryptedHashBytes);

        return unencryptedHash;
    }

    public static String decryptHashFromJson(String filename, PublicKey key) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);
        JsonObject securityObject = rootJson.get("security").getAsJsonObject();

        String encryptedHash = securityObject.get("hash").getAsString();

        return decryptHash(encryptedHash, key);
    }

    public static boolean isClientHashValid(String hash, String filename, String field) throws Exception{
        final String DIGEST_ALGO = "SHA-256";

        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);

        JsonObject object = rootJson.get(field).getAsJsonObject();
        byte[] bytes = object.toString().getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(bytes);
        byte[] digestBytes = messageDigest.digest();
        
        return Base64.getEncoder().encodeToString(digestBytes).equals(hash);
    }

    public static boolean isClientTimestampValid(String filename, String field) throws Exception{
        FileReader fileReader = new FileReader(filename);
        Gson gson = new Gson();
        JsonObject rootJson = gson.fromJson(fileReader, JsonObject.class);

        JsonObject object = rootJson.get(field).getAsJsonObject();
        String timestamp = object.get("timestamp").getAsString();

        Date date = new Date();
        long timeCurrent = date.getTime();

        long timestampLong = Long.parseLong(timestamp);

        //2 minutes
        long buffer = 120000;

        long upperlimit = timeCurrent + buffer;
        long lowerlimit = timeCurrent - buffer;

        if (lowerlimit <= timestampLong && timestampLong <= upperlimit){ return true; }
        return false;
    }

}