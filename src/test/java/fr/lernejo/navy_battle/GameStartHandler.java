package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameStartHandlerTest {

    @Test
    void handle_WithValidPostRequest() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        GameStartHandler handler = new GameStartHandler(8080);
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(202, os.toByteArray().length);

        String responseString = os.toString(StandardCharsets.UTF_8);
        assertThat(responseString).contains("\"id\":");
        assertThat(responseString).contains("\"url\":\"http://localhost:8080\"");
        assertThat(responseString).contains("\"message\":\"May the best code win\"");
        // Vous pouvez ajouter plus de vérifications ici pour vous assurer que la réponse JSON est correcte.
    }

    @Test
    void handle_WithInvalidRequestMethod() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        GameStartHandler handler = new GameStartHandler(8080);
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(404, 0); // Vérifie que le code de réponse est 404

        String responseString = os.toString(StandardCharsets.UTF_8);
        assertTrue(responseString.isEmpty()); // Vérifie que le corps de la réponse est vide
    }

    @Test
    void handle_WithInvalidJson() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("Invalid JSON".getBytes(StandardCharsets.UTF_8)));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        GameStartHandler handler = new GameStartHandler(8080);
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(400, 0); // Vérifie que le code de réponse est 400

        String responseString = os.toString(StandardCharsets.UTF_8);
        assertTrue(responseString.isEmpty()); // Vérifie que le corps de la réponse est vide
    }
}
