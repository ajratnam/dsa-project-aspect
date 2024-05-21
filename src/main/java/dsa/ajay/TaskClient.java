package dsa.ajay;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class TaskClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            System.out.println("Connected to server");

            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                List<GenericTask> tasks = (List<GenericTask>) ois.readObject();
                System.out.println("Tasks received");

                for (Task task : tasks) {
                    // Execute the task
                    Object result = task.execute();
                    System.out.println("Task execution result: " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
