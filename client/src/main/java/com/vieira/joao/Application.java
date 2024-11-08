package com.vieira.joao;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(String[] args) {
        String url = "http://localhost:8443/";

        // TODO: Use someking of key management
        Map<String, String> privateKeyPaths = new HashMap<>();
        privateKeyPaths.put("user1", "keys/user1Private.key");
        privateKeyPaths.put("user2", "keys/user2Private.key");
        privateKeyPaths.put("user3", "keys/user3Private.key");

        Map<String, String> publicKeyPaths = new HashMap<>();
        publicKeyPaths.put("user1", "keys/user1Public.pub");
        publicKeyPaths.put("user2", "keys/user2Public.pub");
        publicKeyPaths.put("user3", "keys/user3Public.pub");

        // TODO: Check if the server is online before creating the client
        //  and wait x seconds for it (then shutdown if not found)
        ClientService clientService = new ClientService(url);
        Client client = new CLIClient(clientService, privateKeyPaths, publicKeyPaths);
        client.startClient();
    }

}
