package helper;

import java.lang.reflect.Method;

public final class RequestHandler {
    private final Object controllerInstance;
    private final Method targetMethod;

    public RequestHandler(Object controllerInstance, Method targetMethod) {
        this.controllerInstance = controllerInstance;
        this.targetMethod = targetMethod;
    }

    public Object getControllerInstance() {
        return controllerInstance;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }
}
