package dsa.ajay;

import java.io.Serializable;


public class GlobalContext implements Serializable {
    private static final long serialVersionUID = 1L;
    public int globalVariable;

    public GlobalContext(int globalVariable) {
        this.globalVariable = globalVariable;
    }

    public int performOperation(int a, String b) {
        globalVariable += a;
        System.out.println("Global variable updated: " + globalVariable);
        return globalVariable + b.length();
    }

    public String anotherMethod(String str) {
        System.out.println("Received string: " + str);
        return str.toUpperCase();
    }

    public void yetAnotherMethod() {
        System.out.println("No parameters method called.");
    }
}
