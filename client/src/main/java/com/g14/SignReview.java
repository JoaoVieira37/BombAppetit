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

public class SignReview {
    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java SignReview JSON fieldname privateKey");
            return;
        }
        final String jsonName = args[0];
        final String fieldName = args[1];
        final String privatekeyname = args[2];

        PrivateKey privateKey = AuxFunctions.getPrivateKey(privatekeyname);

        //create hash
        String hash = AuxFunctions.createHash(fieldName, jsonName);

        //add signature field to json and encrypt using private key

        JsonObject digitalSignature = AuxFunctions.createDigitalSignature(hash, privateKey);

        AuxFunctions.addSecurity(jsonName, digitalSignature);
        
    }
}