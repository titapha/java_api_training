package fr.lernejo.navy_battle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.io.IOException;
import java.net.http.HttpRequest.BodyPublishers;


public class AdversaryContact {
    private final String adversaryUrl;
    private final int myPort;

    public AdversaryContact(String adversaryUrl, int myPort) {
        this.adversaryUrl = adversaryUrl;
        this.myPort = myPort;
    }

    public void sendPostRequest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(adversaryUrl + "/api/game/start"))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + myPort + "\", \"message\":\"hello\"}"))
            .build();

        try {
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

