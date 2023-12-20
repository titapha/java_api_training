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

        Map<String, String> queryParams = queryToMap(exchange.getRequestURI());
        String cell = queryParams.get("cell");

        if (cell == null || !isValidCell(cell)) {
            sendResponse(exchange, 400, ""); // Bad Request for invalid or missing cell parameter
            return;
        }

        JSONObject response = new JSONObject();
        // Ici, vous devez déterminer le résultat du tir sur la cellule (miss, hit, sunk)
        // Pour l'exemple, on utilise des valeurs fixes, mais cela devrait dépendre de l'état actuel du jeu
        response.put("consequence", "miss"); // ou "hit" ou "sunk"
        response.put("shipLeft", true); // ou false si c'était le dernier bateau

        sendResponse(exchange, 200, response.toJSONString());
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
        // Vérifiez ici si la cellule est valide (ex. "B2", "J10", etc.)
        return cell.matches("^[A-J](10|[1-9])$");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } finally {
            exchange.close();
        }
    }
}

