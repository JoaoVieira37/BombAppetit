package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class RSAkeyPairCreator {
    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java RSAkeyPairCreator nameOfPrivateKeyFile nameOfPublicKeyFile");
            return;
        }

        int KEYSIZE = 2048;
        final String privatekeyname = args[0];
        final String publickeyname = args[1];

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        kpg.initialize(KEYSIZE);
        KeyPair kp = kpg.generateKeyPair();

        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();

        FileOutputStream out = new FileOutputStream(privatekeyname + ".key");
        out.write(pvt.getEncoded());
        out.close();

        out = new FileOutputStream(publickeyname + ".pub");
        out.write(pub.getEncoded());
        out.close();

        System.err.println("Private key format: " + pvt.getFormat());
        System.err.println("Public key format: " + pub.getFormat());

        
    }
}