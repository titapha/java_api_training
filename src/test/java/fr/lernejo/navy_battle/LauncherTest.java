package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;

import static org.mockito.Mockito.*;

class LauncherTest {

    @Test
    void startServer_shouldStartServerAndCreateContexts() throws IOException {
        // Mocking the HttpServer and HttpHandler
        HttpServer server = mock(HttpServer.class);
        when(HttpServer.create(any(InetSocketAddress.class), anyInt())).thenReturn(server);

        Launcher launcher = new Launcher(8080, "http://localhost:9876");

        // Appel de la méthode à tester
        launcher.startServer();

        // Vérification que le serveur a démarré et que les contexts ont été créés
        verify(server).createContext(eq("/ping"), any(HttpHandler.class));
        verify(server).createContext(eq("/api/game/start"), any(HttpHandler.class));
        verify(server).createContext(eq("/api/game/fire"), any(HttpHandler.class));
        verify(server).start();

        // Si vous voulez également tester la logique d'AdversaryContact
        // Vous devrez probablement mocker AdversaryContact et injecter le mock dans Launcher
    }
}
