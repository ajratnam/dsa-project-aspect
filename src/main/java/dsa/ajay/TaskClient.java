package dsa.ajay;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

public class TaskClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to server");
            System.out.println("------Executing benchmark------");

            double duration = Benchmark.getNormalizedPerformanceScore();
            oos.writeObject(duration);

            while (true) {
                List<GenericTask> tasks = (List<GenericTask>) ois.readObject();
                System.out.println("Tasks received");
                for (Task task : tasks) {
                    try {
                        // Redirect stdout to a ByteArrayOutputStream
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);
                        PrintStream old = System.out;
                        System.setOut(ps);

                        // Execute the task
                        Object result = task.execute();
                        System.out.println("Task execution result: " + result);

                        // Reset stdout
                        System.out.flush();
                        System.setOut(old);

                        // Send the stdout back to the server
                        oos.writeObject(baos.toString());
                    } catch (Exception e) {
                        // Send the exception back to the server
                        oos.writeObject(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}