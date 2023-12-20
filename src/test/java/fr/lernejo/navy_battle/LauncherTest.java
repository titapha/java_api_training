package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;
import static org.mockito.Mockito.*;

class LauncherTest {

    @Test
    void startServer_shouldStartServerAndCreateContexts() throws IOException {
        HttpServerFactory mockFactory = mock(HttpServerFactory.class);
        HttpServer mockServer = mock(HttpServer.class);
        when(mockFactory.createHttpServer(any(InetSocketAddress.class))).thenReturn(mockServer);

        Launcher launcher = new Launcher(8080, "http://localhost:9876", mockFactory);
        launcher.startServer();

        verify(mockServer).createContext(eq("/ping"), any());
        verify(mockServer).createContext(eq("/api/game/start"), any());
        verify(mockServer).setExecutor(null);
        verify(mockServer).start();
    }
}
