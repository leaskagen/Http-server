package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6969);

        Socket clientSocket = serverSocket.accept();

        String html = "<p>Hello World</p>";

        String contentType = "text/html";

        String response = "HTTP/1.1 200 Morrapuler\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + html.length() + "\r\n" +
                "Connection: close \r\n" +
                "\r\n" +
                html;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
