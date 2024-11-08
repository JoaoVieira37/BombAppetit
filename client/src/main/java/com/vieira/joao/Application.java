package com.vieira.joao;

public class Application {

    public static void main(String[] args) {
        String url = "http://localhost:8443/";

        ClientService clientService = new ClientService(url);
        Client client = new CLIClient(clientService);
        client.startClient();
    }

}
