package wrapper;

import java.util.HashMap;
import java.util.Map;

public class MySession {
    private final Map<String,Object> map;

    public MySession() {
        this.map = new HashMap<>();
    }

    public Object getValue(String key){
        return map.get(key);
    }

    public void add(String key,Object obj){
        map.put(key, obj);
    }
}
