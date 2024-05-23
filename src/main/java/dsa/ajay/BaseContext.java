package dsa.ajay;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class BaseContext implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<GenericTask> tasks = new ArrayList<>();

    public BaseContext(){}

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