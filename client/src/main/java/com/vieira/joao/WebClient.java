package com.vieira.joao;

import com.vieira.joao.ClientService;

public class WebClient implements Client {

    private final ClientService clientService;

    public WebClient(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void startClient() {
        // Implement the web client logic here
        // For example, using Spring Boot to create a web application
    }
}