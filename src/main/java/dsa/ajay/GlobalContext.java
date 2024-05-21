package dsa.ajay;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class GlobalContext implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;
    public int globalVariable;
    private final List<GenericTask> tasks = new ArrayList<>();

    public GlobalContext(){}

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

    public GlobalContext getProxy() {
        return (GlobalContext) Proxy.newProxyInstance(GlobalContext.class.getClassLoader(),
                new Class[]{GlobalContext.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        tasks.add(new GenericTask(this, method.getName(), method.getParameterTypes(), args));
        return method.invoke(this, args);
    }

    public List<GenericTask> getTasks() {
        return tasks;
    }
}