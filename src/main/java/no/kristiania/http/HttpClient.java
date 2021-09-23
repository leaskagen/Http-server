package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpClient {

    public static void main(String[] args) throws IOException {

        // Socket som lager kanal til en port
        Socket socket = new Socket("httpbin.org", 80);

        // Skriver en request til server
        socket.getOutputStream().write(
                ("GET /html HTTP/1.1\r\n" +
                        "Host: httpbin.org\r\n" +
                        "\r\n").getBytes()
        );

        // Svar fra server
        InputStream in = socket.getInputStream();

        // Skriver ut alle karakterene
        int c;
        while ((c = in.read()) != -1){
            System.out.print((char)c);
        }
    }
}
