package fr.lernejo.navy_battle;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.mockito.Mockito.*;

class AdversaryContactTest {

    @Test
    void sendPostRequest_ShouldSendCorrectRequest() throws IOException, InterruptedException {
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        AdversaryContact contact = new AdversaryContact("http://localhost:9876", 9876, mockHttpClient);
        contact.sendPostRequest();

        verify(mockHttpClient).send(argThat(request ->
            request.method().equals("POST") &&
                request.uri().toString().equals("http://localhost:9876/api/game/start") &&
                request.headers().firstValue("Accept").orElse("").equals("application/json") &&
                request.headers().firstValue("Content-Type").orElse("").equals("application/json") &&
                request.bodyPublisher().isPresent()
        ), any(HttpResponse.BodyHandler.class));
    }
}
