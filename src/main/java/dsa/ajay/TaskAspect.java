package dsa.ajay;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.ArrayList;
import java.util.List;

@Aspect
public class TaskAspect {
    private List<GenericTask> tasks = new ArrayList<>();
    private GlobalContext targetObject;

    @Pointcut("execution(public * GlobalContext.*(..))")
    public void globalContextMethods() {}

    @Around("globalContextMethods()")
    public Object aroundGlobalContextMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        tasks.add(new GenericTask(targetObject, joinPoint.getSignature().getName(), parameterTypes, args));
        return joinPoint.proceed();
    }

    public void setTargetObject(GlobalContext targetObject) {
        this.targetObject = targetObject;
    }

    public List<GenericTask> getTasks() {
        return this.tasks;
    }
}