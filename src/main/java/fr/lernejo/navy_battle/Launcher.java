package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.BodyPublishers;
import java.net.URI;

public class Launcher {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Launcher <port> [adversaryUrl]");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(1));
            server.createContext("/ping", new PingHandler());
            server.createContext("/api/game/start", new GameStartHandler(port));
            server.start();
            System.out.println("Server started on port " + port);

            // Si un URL d'adversaire est fourni, envoyez une requête POST
            if (args.length > 1) {
                String adversaryUrl = args[1];
                sendPostRequestToAdversary(adversaryUrl, port);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour envoyer une requête POST à l'URL de l'adversaire
    private static void sendPostRequestToAdversary(String adversaryUrl, int myPort) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(adversaryUrl + "/api/game/start"))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + myPort + "\", \"message\":\"hello\"}"))
            .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("POST request sent to " + adversaryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class GameStartHandler implements HttpHandler {
        private final int port;
        public GameStartHandler(int port) { this.port = port; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 404, "");
                return;
            }
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (requestBody.isEmpty() || !isValidJson(requestBody)) {
                sendResponse(exchange, 400, ""); // Bad Request for empty or malformed JSON
                return;
            }
            sendResponseWithServerDetails(exchange);
        }
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
        private boolean isValidJson(String json) {
            try {
                new JSONParser().parse(json);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        private void sendResponseWithServerDetails(HttpExchange exchange) throws IOException {
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", "some-id");
            responseJson.put("url", "http://localhost:" + port);
            responseJson.put("message", "May the best code win");
            sendResponse(exchange, 202, responseJson.toJSONString());
        }
    }
}

