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
        int port = Integer.parseInt(args[0]);
        try {
            HttpServer server = setupHttpServer(port);
            server.start();
            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpServer setupHttpServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(1));
        setupPingContext(server);
        setupGameStartContext(server, port);
        return server;
    }

    private static void setupPingContext(HttpServer server) {
        server.createContext("/ping", (exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }));
    }

    private static void setupGameStartContext(HttpServer server, int port) {
        server.createContext("/api/game/start", new GameStartHandler(port));
    }

    static class GameStartHandler implements HttpHandler {
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

            String requestBody = getRequestBody(exchange);
            if (requestBody == null || !isValidJson(requestBody)) {
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

