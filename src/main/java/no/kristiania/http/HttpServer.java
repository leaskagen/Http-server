package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final ServerSocket serverSocket;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);

        new Thread(this::handleClients).start();

    }

    private void handleClients() {
        try {
            Socket clientSocket = serverSocket.accept();
            String response = "HTTP/1.1 404 Not found\r\nContent-Length: 0\r\n\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

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
