package wrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MySession {
    private final Map<String, Object> map;

    public MySession() {
        this.map = new ConcurrentHashMap<>();
    }

    public Object getValue(String key) {
        return map.get(key);
    }

    public void add(String key, Object obj) {
        map.put(key, obj);
    }

    public void remove(String key) {
        map.remove(key);
    }

    // NOUVEAU : Permet au FrontServlet de lire les modifications
    public Map<String, Object> getMap() {
        return map;
    }
}