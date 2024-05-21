package dsa.ajay;

import org.aspectj.lang.Aspects;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TaskServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected");

                    // Create an instance of the target object (e.g., GlobalContext)
                    GlobalContext targetObject = new GlobalContext(10);

                    TaskAspect aspect = Aspects.aspectOf(TaskAspect.class);
                    aspect.setTargetObject(targetObject);

                    // Now, whenever you call a method on targetObject, a GenericTask will be automatically created and added to tasks
                    targetObject.performOperation(5, "Test");
                    targetObject.anotherMethod("Hello");
                    targetObject.yetAnotherMethod();

                    // Get the tasks from the aspect
                    List<GenericTask> tasks = aspect.getTasks();

                    // Send the tasks
                    try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                        oos.writeObject(tasks);
                        oos.flush();
                        System.out.println("Tasks sent to client");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
