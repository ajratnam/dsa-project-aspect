package dsa.ajay;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

public class TaskClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to server");
            System.out.println("------Executing benchmark------");

            KeyPair keyPair = RSAUtil.generateKeyPair();

            oos.writeObject(keyPair.getPublic());
            System.out.println("Public key sent to server");

            PublicKey serverPublicKey = (PublicKey) ois.readObject();
            System.out.println("Public key received from server");

            double duration = Benchmark.getNormalizedPerformanceScore();
            oos.writeObject(duration);
            while (true) {
                List<GenericTask> tasks = (List<GenericTask>) ois.readObject();
                System.out.println("Tasks received");
                for (Task task : tasks) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);
                        PrintStream old = System.out;
                        System.setOut(ps);

                        Object result = task.execute();
                        System.out.println("Task execution result: " + result);

                        System.out.flush();
                        System.setOut(old);

                        byte[] encryptedOutput = RSAUtil.encrypt(baos.toString(), serverPublicKey);
                        oos.writeObject(encryptedOutput);
                    } catch (Exception e) {
                        oos.writeObject(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}