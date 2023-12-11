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

public class Launcher {

    // ... Other parts of the Launcher class ...

    static class GameStartHandler implements HttpHandler {
        private final int port;

        public GameStartHandler(int port) {
            this.port = port;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // ... Implementation of handle method ...
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        }

        private String getRequestBody(HttpExchange exchange) throws IOException {
            try (InputStream is = exchange.getRequestBody()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private boolean isValidJson(String json) {
            JSONParser parser = new JSONParser();
            try {
                parser.parse(json);
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

            String response = responseJson.toJSONString();
            sendResponse(exchange, 202, response);
        }
    }
}

