package dsa.ajay;

import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TaskServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345");

            int clientCount = 0;
            Socket[] clients = new Socket[3];
            ObjectOutputStream[] clientStreams = new ObjectOutputStream[3];

            while (clientCount < 3) {
                clients[clientCount] = serverSocket.accept();
                clientStreams[clientCount] = new ObjectOutputStream(clients[clientCount].getOutputStream());
                System.out.println("Client connected");
                clientCount++;
            }

            // Create an instance of the target object (e.g., GlobalContext)
            GlobalContext context = new GlobalContext(10);
            GlobalContext targetObject = (GlobalContext) ObjectGenerator.generateTargetObject(context);

            // Now, whenever you call a method on targetObject, a GenericTask will be automatically created and added to tasks
            targetObject.performOperation(5, "Test");
            targetObject.anotherMethod("Hello");
            targetObject.yetAnotherMethod();

            // Get the tasks from the context
            List<GenericTask> tasks = context.getTasks();

            // Send the tasks
            for (int i = 0; i < 3; i++) {
                clientStreams[i].writeObject(List.of(tasks.get(i)));
                clientStreams[i].flush();
                System.out.println("Task sent to client " + (i + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}