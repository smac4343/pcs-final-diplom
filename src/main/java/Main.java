import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        // Initialize and test search engine
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        // Initialize server socket
        try (ServerSocket serverSocket = new ServerSocket(8989)) { 
            System.out.println("Server started...");

            while (true) { 
                // Handle each client connection
                try (
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    // Read the client's request and perform the search
                    String request = in.readLine();
                    String response = new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(engine.search(request));

                    // Send response to client
                    out.println(response);
                } catch (IOException e) {
                    System.out.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
