package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.OutputStream;

public class Launcher {
    private final int port;
    private final String adversaryUrl;

    public Launcher(int port, String adversaryUrl) {
        this.port = port;
        this.adversaryUrl = adversaryUrl;
    }

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(1));
            server.createContext("/ping", new PingHandler());
            server.createContext("/api/game/start", new GameStartHandler(port));
            server.createContext("/api/game/fire", new FireHandler()); // Ligne ajoutée pour FireHandler
            server.start();
            System.out.println("Server started on port " + port);

            if (adversaryUrl != null) {
                new AdversaryContact(adversaryUrl, port).sendPostRequest();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Launcher <port> [adversaryUrl]");
            return;
        }
        int port = Integer.parseInt(args[0]);
        String adversaryUrl = args.length > 1 ? args[1] : null;
        new Launcher(port, adversaryUrl).startServer();
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
}
