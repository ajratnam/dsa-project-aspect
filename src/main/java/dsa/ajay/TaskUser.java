package dsa.ajay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class TaskUser {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 54321);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to server");

            KeyPair keyPair = RSAUtil.generateKeyPair();
            System.out.println("Key pair generated");

            oos.writeObject(keyPair.getPublic());
            System.out.println("Public key sent to server");

            PublicKey serverPublicKey = (PublicKey) ois.readObject();
            System.out.println("Public key received from server");

            GlobalContext context = new GlobalContext(10);
            GlobalContext targetObject = (GlobalContext) ObjectGenerator.generateTargetObject(context);
            System.out.println("Proxy object generated");
//            targetObject = context;

            targetObject.performOperation(5, "Test");
            targetObject.anotherMethod("Hello");
            targetObject.yetAnotherMethod();

            List<GenericTask> tasks = context.getTasks();

            oos.writeObject(tasks);
            System.out.println("Tasks sent to server\n");

            for (int i = 0; i < tasks.size(); i++) {
                byte[] encryptedResult = (byte[]) ois.readObject();
                String result = RSAUtil.decrypt(encryptedResult, keyPair.getPrivate());
                System.out.print(result);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}