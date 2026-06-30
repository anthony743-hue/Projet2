package listener;

import java.lang.reflect.Method;
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
        try {
            String packageName = sce.getServletContext().getInitParameter("packageName");
            List<Class<?>> listClass = Utility.getAnnotedClasses(Controller.class,
                    Utility.getClassInPackage(packageName));
            Utility.getMapUrlMapping(listClass, map);

            sce.getServletContext().setAttribute("mapUrl", map);
            sce.getServletContext().setAttribute("listClass", listClass);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
