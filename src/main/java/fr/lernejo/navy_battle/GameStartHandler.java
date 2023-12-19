package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameStartHandler implements HttpHandler {
    private final int port;

    public GameStartHandler(int port) {
        this.port = port;
    }

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
        responseJson.put("id", "some-id"); // Mettez ici l'ID approprié
        responseJson.put("url", "http://localhost:" + port); // L'URL doit correspondre à l'adresse du serveur
        responseJson.put("message", "May the best code win"); // Message personnalisable

        String jsonResponse = responseJson.toJSONString();
        sendResponse(exchange, 202, jsonResponse); // Envoi de la réponse JSON
    }
}
