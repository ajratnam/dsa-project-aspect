package dsa.ajay;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

public class ObjectGenerator {

    public static Object generateTargetObject(Object givenObject) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(givenObject.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                ((BaseContext) givenObject).getTasks().add(new GenericTask(givenObject, method.getName(), method.getParameterTypes(), args));
                return null;
            }
        });
        return enhancer.create();
    }
}