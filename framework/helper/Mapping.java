package helper;

import java.lang.reflect.Method;

public final class Mapping {
    private final String controller;
    private final Method methode;

    public Mapping(String controller, Method methode) {
        this.controller = controller;
        this.methode = methode;
    }

    public String getController() {
        return controller;
    }

    public Method getMethode() {
        return methode;
    }
}
