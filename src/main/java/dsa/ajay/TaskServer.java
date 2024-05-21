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

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected");

                    // Create an instance of the target object (e.g., GlobalContext)
                    GlobalContext context = new GlobalContext(10);
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(GlobalContext.class);
                    enhancer.setCallback(new MethodInterceptor() {
                        @Override
                        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                            // Add the GenericTask to the tasks list without invoking the method
                            context.getTasks().add(new GenericTask(context, method.getName(), method.getParameterTypes(), args));
                            return null;
                        }
                    });
                    GlobalContext targetObject = (GlobalContext) enhancer.create();

                    // Now, whenever you call a method on targetObject, a GenericTask will be automatically created and added to tasks
                    targetObject.performOperation(5, "Test");
                    targetObject.anotherMethod("Hello");
                    targetObject.yetAnotherMethod();

                    // Get the tasks from the context
                    List<GenericTask> tasks = context.getTasks();

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