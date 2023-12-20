package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Launcher {
    private final int port;
    private final String adversaryUrl;
    private final HttpServer server;

    public Launcher(int port, String adversaryUrl, HttpServer server) {
        this.port = port;
        this.adversaryUrl = adversaryUrl;
        this.server = server;
    }

    public void startServer() throws IOException {
        server.bind(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(1));
        server.createContext("/ping", new PingHandler());
        server.createContext("/api/game/start", new GameStartHandler(port));
        server.createContext("/api/game/fire", new FireHandler());
        server.start();

        System.out.println("Server started on port " + port);

        if (adversaryUrl != null) {
            new AdversaryContact(adversaryUrl, port).sendPostRequest();
        }
    }

    static class PingHandler implements HttpHandler {
        @Override
        public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
            // PingHandler implementation
        }
    }

    // Include other inner classes like GameStartHandler, FireHandler, etc.
}
