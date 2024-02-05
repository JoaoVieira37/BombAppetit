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

public class Protect {

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

        if(!voucherexists){
            System.out.println("Voucher does not exist, cannot be encrypted!");
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
}