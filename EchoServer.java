import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer {

    private static final int MAX_ENTRIES = 5;

    private static final Map<String, String> store = new LinkedHashMap<>(16, 0.75f, true){
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest){
            return size() > MAX_ENTRIES;
        }
    };

    private static final Map<String, Long> expirationTimes = new HashMap<>();

    public static void main(String[] args) throws IOException{
        int port = 6379;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");

            Thread clientThread = new Thread (() -> handleClient(clientSocket));
            clientThread.start();
        }

    }
        private static void handleClient(Socket clientSocket) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null){
                System.out.println("Received: " + line);
                String response = handleCommand(line);
                out.println(response);
        }
    
        clientSocket.close();
        System.out.println("Client disconnected.");
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }
        private static String handleCommand(String line){
            String[] parts = line.trim().split("\\s+");
            if(parts.length == 0 || parts[0].isEmpty()){
                return "ERR empty command";
        }

        String command = parts[0].toUpperCase();

        switch (command) {
            case "SET":
                if (parts.length < 3) {
                    return "ERR wrong number of arguments for SET";
                }
                String key = parts[1];
                String value = parts[2];
                store.put(key, value);
                return "OK";

            case "GET":
                if(parts.length < 2) {
                    return "ERR wrong number of arguments for GET";
                }               
                String getKey = parts[1];

                if (expirationTimes.containsKey(getKey)) {
                    long expireAt = expirationTimes.get(getKey);
                    if (System.currentTimeMillis() >= expireAt) {
                        store.remove(getKey);
                        expirationTimes.remove(getKey);
                        return "(nil)";
                    }
                }
                
                String result = store.get(getKey);
                return (result != null) ? result : "(nil)";
            
            case "DELETE":
                if (parts.length < 2) {
                    return "ERR wrong number of arguments for DELETE";
                }
                String deleteKey = parts[1];
                boolean existed = store.remove(deleteKey) != null;
                expirationTimes.remove(deleteKey);
                return existed ? "OK" : "(nil)";
            
            case "EXPIRE":
                if(parts.length < 3) {
                    return "ERR wrong number of arguments for EXPIRE";
                }
                String expireKey = parts[1];
                if (!store.containsKey(expireKey)) {
                    return "(nil)";
                }
                try {
                    int seconds = Integer.parseInt(parts[2]);
                    long expireAt = System.currentTimeMillis() + (seconds * 1000L);
                    expirationTimes.put(expireKey, expireAt);
                    return "OK";
                } catch (NumberFormatException e) {
                    return "ERR seconds must be an integer";
                }
            
            default:
                return "ERR unknown command ' " + command + " ' ";
        }
    }
}
