package com.g14;

public class Help {

    public static void main(String[] args) throws Exception {

        System.out.println("Available commands:");
        System.out.println("");
        System.out.println("Protect - Protects a json file by adding security");
        System.out.println("Usage:");
        System.out.println("protect <input json> <output json> <client public key> <server private key>");
        System.out.println("");
        System.out.println("Check - Checks if a json file is currently protected");
        System.out.println("Usage:");
        System.out.println("check <json>");
        System.out.println("");
        System.out.println("Unprotect - Removes security from a json file");
        System.out.println("Usage:");
        System.out.println("unprotect <input json> <output json> <client private key>");
        System.out.println("");
        System.out.println("Create RSA key pair - creates a pair of RSA keys (public and private)");
        System.out.println("Usage:");
        System.out.println("createRSA <name of public key file> <name of private key file>");
        

    }

}