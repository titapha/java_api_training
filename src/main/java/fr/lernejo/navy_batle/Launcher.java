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

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]); // Get port from the first argument
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(1)); // Create an executor with one thread

            // Context for "/ping"
            server.createContext("/ping", (exchange -> {
                String response = "OK";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }));

            // Context for "/api/game/start"
            server.createContext("/api/game/start", new GameStartHandler(port));

            server.start();
            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class GameStartHandler implements HttpHandler {
        private final int port;

        public GameStartHandler(int port) {
            this.port = port;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(404, -1); // Not Found for non-POST requests
                return;
            }

            try {
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(requestBody);

                // Respond with the same JSON structure
                JSONObject responseJson = new JSONObject();
                responseJson.put("id", "some-id"); // Replace with actual server id
                responseJson.put("url", "http://localhost:" + port);
                responseJson.put("message", "May the best code win");

                String response = responseJson.toJSONString();
                exchange.sendResponseHeaders(202, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (ParseException e) {
                exchange.sendResponseHeaders(400, -1); // Bad Request for malformed JSON
            }
        }
    }
}

