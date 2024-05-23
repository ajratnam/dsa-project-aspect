package dsa.ajay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskServer {

    private static final List<Socket> clients = new ArrayList<>();
    private static final List<ObjectOutputStream> clientStreams = new ArrayList<>();
    private static final List<ObjectInputStream> clientInputStreams = new ArrayList<>();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Thread for accepting client connections
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server is listening on port 12345 for clients");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clients.add(clientSocket);
                    clientStreams.add(new ObjectOutputStream(clientSocket.getOutputStream()));
                    clientInputStreams.add(new ObjectInputStream(clientSocket.getInputStream()));

                    double duration = (double) clientInputStreams.getLast().readObject();
                    System.out.println("Duration received from client " + clients.toArray().length + ": " + duration);

                    System.out.println("Client connected");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Thread for accepting user connections
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(54321)) {
                System.out.println("Server is listening on port 54321 for users");

                while (true) {
                    Socket userSocket = serverSocket.accept();
                    ObjectOutputStream userOutputStream = new ObjectOutputStream(userSocket.getOutputStream());
                    ObjectInputStream userInputStream = new ObjectInputStream(userSocket.getInputStream());

                    System.out.println("User connected");

                    // Receive tasks from user
                    List<GenericTask> tasks = (List<GenericTask>) userInputStream.readObject();

                    // Distribute tasks among clients
                    for (int i = 0; i < tasks.size(); i++) {
                        clientStreams.get(i % clients.size()).writeObject(List.of(tasks.get(i)));
                    }

                    // Receive results from clients and send them back to the user
                    for (int i = 0; i < tasks.size(); i++) {
                        Object result = clientInputStreams.get(i % clients.size()).readObject();
                        userOutputStream.writeObject(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }
}