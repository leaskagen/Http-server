package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);

        new Thread(this::handleClients).start();

    }

    // En thread som kan ta i mot request fra client og gi svar til client samtidig
    private void handleClients() {
        try { // Try metoden vil lukke connection til porten når den er ferdig med å gi svar
            Socket clientSocket = serverSocket.accept();

            String[] requestLine = HttpClient.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];

            if( requestTarget.equals("/hello")){
                String responseText = "Hello world";

                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: " + responseText.length() + "\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n" +
                        responseText;

                clientSocket.getOutputStream().write(response.getBytes());
            } else {
                if(rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))){
                    String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: " + responseText.length() + "\r\n" +
                            "Content-Type: text/html\r\n" +
                            "\r\n" +
                            responseText;

                    clientSocket.getOutputStream().write(response.getBytes());
                }


                String responseText = "File not found: " + requestTarget;

                String response = "HTTP/1.1 404 Not found\r\n" +
                        "Content-Length: " + responseText.length() + "\r\n" +
                        "\r\n" +
                        responseText;

                clientSocket.getOutputStream().write(response.getBytes());
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(6969);
        httpServer.setRoot(Paths.get("."));
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
