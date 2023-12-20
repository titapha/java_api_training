package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.mockito.Mockito.*;

class LauncherTest {

    @Test
    void startServer_shouldStartServerAndCreateContexts() throws IOException {
        HttpServer mockServer = mock(HttpServer.class);
        Launcher launcher = new Launcher(8080, "http://localhost:9876", mockServer);

        launcher.startServer();

        verify(mockServer).createContext(eq("/ping"), any());
        verify(mockServer).createContext(eq("/api/game/start"), any());
        verify(mockServer).createContext(eq("/api/game/fire"), any());
        verify(mockServer).start();
    }
}
