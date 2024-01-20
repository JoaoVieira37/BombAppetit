package sirs.a14.bombappetit;

import java.security.PublicKey;

public class VerifyClientJsonIntegrity {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.print("Usage: java VerifyClientJsonIntegrity JSON clientPublicKey");
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

        if (isTime && isHash) {
            System.out.println("is valid undefined TODO");
        }
    }

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
}