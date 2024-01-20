package com.g14;

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

public class VerifyClientJsonIntegrity{

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java VerifyClientJsonIntegrity JSON clientPublicKey");
            return;
        }

        final String JSONname = args[0];
        final String publicKeyName = args[1];

        //get client public key
        PublicKey publicKey = AuxFunctions.getPublicKey(publicKeyName);

        //decrypt hash
        String hash = AuxFunctions.decryptHashFromJson(JSONname, publicKey);

        //check hash value
        boolean isHash = AuxFunctions.isClientHashValid(hash, JSONname, "info");

        //check timestamp
        boolean isTime = AuxFunctions.isClientTimestampValid(JSONname, "info");
        
        if (isTime && isHash){
            System.out.println("is valid undefined TODO");
        }
    }
}