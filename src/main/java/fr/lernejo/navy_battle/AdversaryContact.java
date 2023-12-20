package fr.lernejo.navy_battle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.io.IOException;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class AdversaryContact {
    private final String adversaryUrl;
    private final int myPort;
    private final HttpClient httpClient;

    // Constructeur pour utilisation normale
    public AdversaryContact(String adversaryUrl, int myPort) {
        this.adversaryUrl = adversaryUrl;
        this.myPort = myPort;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Constructeur pour les tests avec injection de dépendances
    public AdversaryContact(String adversaryUrl, int myPort, HttpClient httpClient) {
        this.adversaryUrl = adversaryUrl;
        this.myPort = myPort;
        this.httpClient = httpClient;
    }

    public void sendPostRequest() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(adversaryUrl + "/api/game/start"))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + myPort + "\", \"message\":\"hello\"}"))
            .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
