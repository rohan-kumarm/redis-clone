import java.io.*;
import java.net.*;

public class EchoServer {
    public static void main(String[] args) throws IOException{
        int port = 6379;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String line;
        while ((line = in.readLine()) != null){
            System.out.println("Received: " + line);
            out.println("ECHO: " + line);
        }

        clientSocket.close();
        serverSocket.close();
    }
}
