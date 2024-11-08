package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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

    public void getInfo() {
        String endpoint = baseURL + "info";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .build();

        String response = sendRequest(request);

        if (response == null) {
            System.err.println("ERROR: request was not successful");
            return;
        }

        JsonElement jsonElement = JsonParser.parseString(response);
        String prettyJsonString = gson.toJson(jsonElement);

        System.out.println(prettyJsonString);
    }


}