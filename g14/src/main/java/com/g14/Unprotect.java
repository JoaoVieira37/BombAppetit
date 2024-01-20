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

public class Unprotect {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java Unprotect inputJSONfile outputJSONfile clientPrivateKey");
            return;
        }

        final String inputJSONname = args[0];
        final String outputJSONname = args[1];
        final String clientPrivateKeyName = args[2];

        //get client private key
        PrivateKey clientPrivateKey = AuxFunctions.getPrivateKey(clientPrivateKeyName);

        //check if voucher exists, if not exit
        boolean voucherexists = AuxFunctions.voucherExists(inputJSONname);

        if(!voucherexists){
            System.out.println("Voucher does not exist, cannot be decrypted!");
            System.exit(0);
        }

        //create copy of input json
        AuxFunctions.copyFileUsingStream(inputJSONname,outputJSONname);

        //decrypt fields of voucher on new json
        AuxFunctions.decryptVoucherPrivateKey(outputJSONname, clientPrivateKey);

        //remove security (digital signature)
        AuxFunctions.removeSecurity(outputJSONname);
    }
}