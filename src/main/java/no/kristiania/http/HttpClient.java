package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private final int statusCode;
    private final Map<String, String> headerFields = new HashMap<>();
    private String messageBody;

    public HttpClient(String host, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(host, port);

        // Skriver en request
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        // Henter status code som er i første linje på index 1
        String[] statusLine = readLine(socket).split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);

        // Henter verdiene fra headerline
        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) { // Leser hver linje helt til det ikke er flere
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos); // Leser verdi før kolon
            String headerValue = headerLine.substring(colonPos+1).trim(); // Leser verdi etter kolon
            headerFields.put(headerField, headerValue); // Putter verdiene i Map
        }

        this.messageBody = readBytes(socket, getContentLength());
    }

    // Leser hver karakter og teller antall bytes
    private String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char)socket.getInputStream().read());
        }
        return buffer.toString();
    }

    // Leser hver linje
    static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') { // Leser linje helt fram til \r
            buffer.append((char)c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return buffer.toString(); // Sender linje til while i headerline
    }

    public int getStatusCode() { return statusCode; }

    public String getHeader(String headerName) { return headerFields.get(headerName); }

    public int getContentLength() { return Integer.parseInt(getHeader("Content-Length")); }

    public String getMessageBody() { return messageBody; }
}