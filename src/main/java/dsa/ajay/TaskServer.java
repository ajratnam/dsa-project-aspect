package dsa.ajay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskServer {

    private static final AVLTree clientsTree = new AVLTree();

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        KeyPair keyPair = RSAUtil.generateKeyPair();
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server is listening on port 12345 for clients");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream clientStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream clientInputStream = new ObjectInputStream(clientSocket.getInputStream());

                    clientStream.writeObject(keyPair.getPublic());
                    System.out.println("Public key sent to client");

                    PublicKey clientPublicKey = (PublicKey) clientInputStream.readObject();
                    System.out.println("Public key received from client");

                    double duration = 0.0;
                    if (clientSocket.isConnected()) {
                        duration = (double) clientInputStream.readObject();
                        System.out.println("Duration received from client " + (clientsTree.root != null ? clientsTree.root.height : "0") + ": " + duration);

                        clientsTree.root = clientsTree.insert(clientsTree.root, duration, clientSocket, clientStream, clientInputStream, clientPublicKey);
                        System.out.println("Client connected");
                    } else {
                        clientsTree.root = clientsTree.deleteNode(clientsTree.root, duration);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(54321)) {
                System.out.println("Server is listening on port 54321 for users");

                while (true) {
                    Socket userSocket = serverSocket.accept();
                    ObjectOutputStream userOutputStream = new ObjectOutputStream(userSocket.getOutputStream());
                    ObjectInputStream userInputStream = new ObjectInputStream(userSocket.getInputStream());

                    System.out.println("User connected");

                    PublicKey userKey = (PublicKey) userInputStream.readObject();
                    userOutputStream.writeObject(keyPair.getPublic());

                    List<GenericTask> tasks = (List<GenericTask>) userInputStream.readObject();

                    List<ClientNode> clients = clientsTree.inOrderTraversal(clientsTree.root);
                    for (int i = 0; i < tasks.size(); i++) {
                        boolean taskSent = false;
                        int clientIndex = i % clients.size();
                        while (!taskSent) {
                            if (clientIndex >= clients.size()) {
                                clientIndex = 0;
                            }
                            try {
                                ClientNode clientNode = clients.get(clientIndex);
                                if (clientNode.clientSocket.isConnected()) {
                                    clientNode.clientStream.writeObject(List.of(tasks.get(i)));
                                    taskSent = true;
                                } else {
                                    clientsTree.root = clientsTree.deleteNode(clientsTree.root, clientNode.duration);
                                    clients.remove(clientIndex);
                                }
                            } catch (SocketException e) {
                                ClientNode clientNode = clients.get(clientIndex);
                                clientsTree.root = clientsTree.deleteNode(clientsTree.root, clientNode.duration);
                                clients.remove(clientIndex);
                            }
                        }
                    }

                    for (int i = 0; i < tasks.size(); i++) {
                        boolean resultReceived = false;
                        int clientIndex = i % clients.size();
                        while (!resultReceived) {
                            if (clientIndex >= clients.size()) {
                                clientIndex = 0;
                            }
                            try {
                                ClientNode clientNode = clients.get(clientIndex);
                                if (clientNode.clientSocket.isConnected()) {
                                    String result = RSAUtil.decrypt((byte[]) clientNode.clientInputStream.readObject(), keyPair.getPrivate());
                                    userOutputStream.writeObject(RSAUtil.encrypt(result, userKey));
                                    resultReceived = true;
                                } else {
                                    clientsTree.root = clientsTree.deleteNode(clientsTree.root, clientNode.duration);
                                    clients.remove(clientIndex);
                                }
                            } catch (SocketException e) {
                                ClientNode clientNode = clients.get(clientIndex);
                                clientsTree.root = clientsTree.deleteNode(clientsTree.root, clientNode.duration);
                                clients.remove(clientIndex);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }
}