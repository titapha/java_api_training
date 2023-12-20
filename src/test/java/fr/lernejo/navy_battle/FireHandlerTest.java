package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class FireHandlerTest {

    @Test
    void handle_WithInvalidRequestMethod() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");

        FireHandler handler = new FireHandler();
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(404, 0);
    }

    @Test
    void handle_WithInvalidCellParameter() throws IOException {
        HttpExchange exchange = setupHttpExchange("GET", "cell=invalid");

        FireHandler handler = new FireHandler();
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(400, 0);
    }

    @Test
    void handle_WithValidRequest() throws IOException {
        HttpExchange exchange = setupHttpExchange("GET", "cell=B5");

        FireHandler handler = new FireHandler();
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyInt());

        String responseContent = new String(((ByteArrayOutputStream) exchange.getResponseBody()).toByteArray(), StandardCharsets.UTF_8);
        assertThat(responseContent).contains("\"consequence\":");
        assertThat(responseContent).contains("\"shipLeft\":");
        // Vous pouvez ajouter des assertions plus spécifiques si vous connaissez la réponse exacte attendue.
    }


    private HttpExchange setupHttpExchange(String method, String query) throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn(method);
        URI uri = URI.create("http://localhost/test?" + query);
        when(exchange.getRequestURI()).thenReturn(uri);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        return exchange;
    }
}
