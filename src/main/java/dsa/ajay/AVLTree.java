package dsa.ajay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

class ClientNode {
    int height;
    double duration;
    ClientNode left, right;
    Socket clientSocket;
    ObjectOutputStream clientStream;
    ObjectInputStream clientInputStream;
    PublicKey clientPublicKey;

    ClientNode(double duration, Socket clientSocket, ObjectOutputStream clientStream, ObjectInputStream clientInputStream, PublicKey clientPublicKey) {
        this.duration = duration;
        this.clientSocket = clientSocket;
        this.clientStream = clientStream;
        this.clientInputStream = clientInputStream;
        this.clientPublicKey = clientPublicKey;
        height = 1;
    }
}

public class AVLTree {
    ClientNode root;

    int height(ClientNode N) {
        if (N == null)
            return 0;
        return N.height;
    }

    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    ClientNode rightRotate(ClientNode y) {
        ClientNode x = y.left;
        ClientNode T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        return x;
    }

    ClientNode leftRotate(ClientNode x) {
        ClientNode y = x.right;
        ClientNode T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        return y;
    }

    int getBalance(ClientNode N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    ClientNode insert(ClientNode node, double duration, Socket clientSocket, ObjectOutputStream clientStream, ObjectInputStream clientInputStream, PublicKey clientPublicKey) {
        if (node == null)
            return (new ClientNode(duration, clientSocket, clientStream, clientInputStream, clientPublicKey));
        if (duration < node.duration)
            node.left = insert(node.left, duration, clientSocket, clientStream, clientInputStream, clientPublicKey);
        else if (duration > node.duration)
            node.right = insert(node.right, duration, clientSocket, clientStream, clientInputStream, clientPublicKey);
        else
            return node;
        node.height = 1 + max(height(node.left), height(node.right));
        int balance = getBalance(node);
        if (balance > 1 && duration < node.left.duration)
            return rightRotate(node);
        if (balance < -1 && duration > node.right.duration)
            return leftRotate(node);
        if (balance > 1 && duration > node.left.duration) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && duration < node.right.duration) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    ClientNode minValueNode(ClientNode node) {
        ClientNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    ClientNode deleteNode(ClientNode root, double duration) {
        if (root == null)
            return root;
        if (duration < root.duration)
            root.left = deleteNode(root.left, duration);
        else if (duration > root.duration)
            root.right = deleteNode(root.right, duration);
        else {
            if ((root.left == null) || (root.right == null)) {
                ClientNode temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else
                    root = temp;
            } else {
                ClientNode temp = minValueNode(root.right);
                root.duration = temp.duration;
                root.right = deleteNode(root.right, temp.duration);
            }
        }
        if (root == null)
            return root;
        root.height = max(height(root.left), height(root.right)) + 1;
        int balance = getBalance(root);
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
        return root;
    }

    List<ClientNode> inOrderTraversal(ClientNode node) {
        List<ClientNode> result = new ArrayList<>();
        if (node != null) {
            result.addAll(inOrderTraversal(node.left));
            result.add(node);
            result.addAll(inOrderTraversal(node.right));
        }
        return result;
    }
}
