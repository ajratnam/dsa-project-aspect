package dsa.ajay;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

public class ObjectGenerator {

    public static Object generateTargetObject(Object givenObject) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(givenObject.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                // Add the GenericTask to the tasks list without invoking the method
                ((BaseContext) givenObject).getTasks().add(new GenericTask(givenObject, method.getName(), method.getParameterTypes(), args));
                return null;
            }
        });
        return enhancer.create();
    }
}