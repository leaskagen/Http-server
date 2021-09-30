package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTest {

    // Tester for HttpClient
    @Test
    void shouldReturnStatusCode() throws IOException {
        assertEquals(200,
                new HttpClient("httpbin.org", 80, "/html")
                        .getStatusCode());
        assertEquals(404,
                new HttpClient("httpbin.org", 80, "/no-such-page")
                        .getStatusCode());
    }

    @Test
    void shouldReturnHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type"));
    }

    @Test
    void shouldReadContentLength() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals(3741, client.getContentLength());
    }

    @Test
    void shouldReadBody() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertTrue(client.getMessageBody().startsWith("<!DOCTYPE html>"),
                "Expected HTML: " + client.getMessageBody());
    }

    // Tester for HttpServer
    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpServer server = new HttpServer(0);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing" );
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpServer server = new HttpServer(0);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing" );
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        HttpServer server = new HttpServer(0);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello" );
        assertAll( // Kjører flere tester samtidig
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html", client.getHeader("Content-Type")),
                () -> assertEquals("<p>Hello world</p>", client.getMessageBody())
        );
    }

    @Test
    void shouldServeFiles() throws IOException {
        HttpServer server = new HttpServer(0);
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "A file created at " + LocalTime.now();
        Files.write(Paths.get("target/test-classes/example-file.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.txt");
        assertEquals(fileContent, client.getMessageBody());
    }

    @Test
    void shouldUseFileExtensionForContentType() throws IOException {
        HttpServer server = new HttpServer(0);
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "<p>Hello</p>";
        Files.write(Paths.get("target/test-classes/example-file.html"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.html");
        assertEquals("text/html", client.getHeader("Content-Type"));
    }

    @Test
    void shouldEchoQueryParameter() throws IOException {
        HttpServer server = new HttpServer(0);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello?yourName=lea");
        assertEquals("<p>Hello lea</p>", client.getMessageBody());
    }

    @Test
    void shouldHandleMoreThanOneRequest() throws IOException {
        HttpServer server = new HttpServer(0);
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
    }

}