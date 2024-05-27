package dsa.ajay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TaskUser {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 54321);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to server");

            GlobalContext context = new GlobalContext(10);
            GlobalContext targetObject = (GlobalContext) ObjectGenerator.generateTargetObject(context);

            // Now, whenever you call a method on targetObject, a GenericTask will be automatically created and added to tasks
            targetObject.performOperation(5, "Test");
            targetObject.anotherMethod("Hello");
            targetObject.yetAnotherMethod();

            // Get the tasks from the context
            List<GenericTask> tasks = context.getTasks();

            // Send tasks to the server
            oos.writeObject(tasks);
            System.out.println("Tasks sent\n");

            // Receive results from the server
            for (int i = 0; i < tasks.size(); i++) {
                Object result = ois.readObject();
                System.out.print(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}