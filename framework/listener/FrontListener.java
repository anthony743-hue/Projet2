package listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.Controller;
import annotation.UrlMapping;
import helper.HttpMethod;
import helper.Mapping;
import helper.UrlMethod;
import helper.UrlValidator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.Utility;

@WebListener
public class FrontListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<UrlMethod, Mapping> map = new HashMap<>() {
            @Override
            public Mapping put(UrlMethod key, Mapping value) {
                String url = key.getUrl();
                if (!UrlValidator.isValid(url)) {
                    throw new IllegalStateException("Url format invalide");
                }
                if (containsKey(key)) {
                    throw new IllegalStateException("Duplicate mapping detected for URL: " + key);
                }
                return super.put(key, value);
            }
        };
        String packageName = sce.getServletContext().getInitParameter("packageName");
        List<Class<?>> listClass = new ArrayList<>();
        try {
            Utility.getClassInPackage(packageName, listClass);
            Method[] listMethod;
            UrlMethod urlMethod = null;
            Mapping mapping = null;
            String className = null;
            String url = null;
            HttpMethod methode = null;
            for (Class<?> cl : listClass) {
                className = cl.getName();
                if (cl.isAnnotationPresent(Controller.class)) {
                    listMethod = cl.getDeclaredMethods();
                    for (Method m : listMethod) {
                        m.setAccessible(true);
                        if (m.isAnnotationPresent(UrlMapping.class)) {
                            url = m.getAnnotation(UrlMapping.class).url();
                            methode = m.getAnnotation(UrlMapping.class).method();
                            if (methode != null) {
                                urlMethod = new UrlMethod(url, methode);
                                mapping = new Mapping(className, m);
                                map.put(urlMethod, mapping);
                            } else {
                                throw new Exception("Methode non defini pour l'url : " + url);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        sce.getServletContext().setAttribute("mapUrl", map);
        sce.getServletContext().setAttribute("listClass", listClass);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
