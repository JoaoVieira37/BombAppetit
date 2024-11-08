package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientService {

    private final HttpClient httpClient;
    private final String baseURL;
    private final Gson gson;

    public ClientService(String baseURL) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseURL = baseURL;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String sendRequest(HttpRequest request) {
        try {
            // TODO: Try to build a BodyHandler that returns a JsonObject
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public String getRequest(String endpoint) {
        String fullURL = baseURL + endpoint;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullURL))
                .GET()
                .build();

        String response = sendRequest(request);

        if (response == null) {
            System.err.println("ERROR: request was not successful");
            return fullURL;
        }

        JsonElement jsonElement = JsonParser.parseString(response);

        return gson.toJson(jsonElement);
    }

    public String getInfo() {
        return getRequest("info");
    }


    public String getUsers() {
        return getRequest("users");
    }
}