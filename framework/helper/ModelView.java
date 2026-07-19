package helper;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String view;
    public String getView() {
        return view;
    }

    private Map<String,Object> attributes;
    public ModelView(String view) {
        this.view = view;
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String attributeName, Object attrObject){
        attributes.put(attributeName, attrObject);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setView(String view) {
        this.view = view;
    }
}
