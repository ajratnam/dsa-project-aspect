package dsa.ajay;

import java.lang.reflect.Method;

public class GenericTask implements Task {
    private static final long serialVersionUID = 1L;
    private Object targetObject;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public GenericTask(Object targetObject, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.targetObject = targetObject;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    @Override
    public Object execute() throws Exception {
        Method method = targetObject.getClass().getMethod(methodName, parameterTypes);
        return method.invoke(targetObject, parameters);
    }
}
