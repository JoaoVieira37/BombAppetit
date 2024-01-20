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

public class Check {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument missing!");
            System.err.printf("Usage: java Check JSONfile");
            return;
        }

        final String filename = args[0];

        final String serverPrivateKeyFileName = "keys/serverPublic.pub";
        final String nonceFileName = "nonce.txt";

        //check if protected
        boolean isprotected = AuxFunctions.isProtected(filename);
        if (!isprotected){
            System.out.println("File is not protected!");
            System.exit(0);
        }

        //get public key
        PublicKey serverPublicKey = AuxFunctions.getPublicKey(serverPrivateKeyFileName);

        //decrypt security
        List<String> list = AuxFunctions.decryptSecurity(filename, serverPublicKey);

        //check hash
        boolean hashvalid = AuxFunctions.isHashValid(list.get(0),filename);
        if (hashvalid){
            System.out.println("Hash matches file!");
        }
        else{
            System.out.println("Hash does not match file!");
        }

        //check nonce
        boolean noncevalid = AuxFunctions.isNonceValid(list.get(1),nonceFileName);
        if (noncevalid){
            System.out.println("Nonce has a match in database!");
        }
        else{
            System.out.println("Nonce does not have a match in database!");
        }

        //output if document is secure
        if (noncevalid && hashvalid){
            System.out.println("Document is secure!");
        }
        else{
            System.out.println("Document is not secure!");
        }


    }

}