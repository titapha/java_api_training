package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public class FireHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 404, "");
            return;
        }

        String cell = getCellParam(exchange.getRequestURI());
        if (cell == null || !isValidCell(cell)) {
            sendResponse(exchange, 400, "");
            return;
        }

        JSONObject response = buildJsonResponse(cell);
        sendResponse(exchange, 200, response.toJSONString());
    }

    private String getCellParam(URI uri) {
        Map<String, String> queryParams = queryToMap(uri);
        return queryParams.get("cell");
    }

    private Map<String, String> queryToMap(URI uri) {
        Map<String, String> result = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
                } else {
                    result.put(entry[0], "");
                }
            }
        }
        return result;
    }

    private boolean isValidCell(String cell) {
        return cell.matches("^[A-J](10|[1-9])$");
    }

    private JSONObject buildJsonResponse(String cell) {
        JSONObject response = new JSONObject();
        response.put("consequence", determineConsequence(cell)); // Exemple : "miss", "hit", "sunk"
        response.put("shipLeft", shipsRemaining()); // Exemple : true or false
        return response;
    }

    private String determineConsequence(String cell) {
        // Implémentez la logique pour déterminer le résultat du tir
        return "miss"; // Exemple
    }

    private boolean shipsRemaining() {
        // Implémentez la logique pour déterminer s'il reste des navires
        return true; // Exemple
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
        }
    }
}
