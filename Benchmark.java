import java.io.*;
import java.net.*;

public class Benchmark {
    public static void main(String[] args) throws IOException {
        int numRequests = 100000;

        Socket socket = new Socket("localhost", 6379);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numRequests; i++) {
            out.println("SET key" + i + " value" + i);
            in.readLine();
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        double opsPerSecond = numRequests / elapsedSeconds;

        System.out.printf("SET: %d requests in %.2f seconds (%.2f ops/sec)%n", numRequests, elapsedSeconds, opsPerSecond);

        long getStartTime = System.currentTimeMillis();

        for (int i = 0; i < numRequests; i++) {
            out.println("GET key" + i);
            in.readLine();
        }

        long getEndTime = System.currentTimeMillis();
        double getElapsedSeconds = (getEndTime - getStartTime) / 1000.0;
        double getOpsPerSecond = numRequests / getElapsedSeconds;

        System.out.printf("GET: %d requests in %.2f seconds (%.2f ops/sec)%n", numRequests, getElapsedSeconds, getOpsPerSecond);
        socket.close();
    }
}