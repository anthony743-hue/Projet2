package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utils.Utility;
import annotation.Controller;
import annotation.UrlMapping;
import helper.HttpMethod;
import helper.Mapping;
import helper.UrlMethod;
import helper.UrlValidator;

public class FrontServlet extends HttpServlet {
    private List<Class<?>> listClass;
    private Map<UrlMethod, Mapping> registry;

    public void init() throws ServletException {
        registry = new HashMap<>() {
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

        String packageName = this.getInitParameter("packageName");
        try {
            listClass = Utility.getAnnotedClasses(Controller.class, Utility.getClassInPackage(packageName));
            List<Method> listMethod = null;
            UrlMethod urlMethod = null;
            Mapping mapping = null;
            String className = null;
            String url = null;
            HttpMethod methode = null;

            for (Class<?> cl : listClass) {
                className = cl.getName();
                listMethod = Utility.getAnnotedMethod(cl, UrlMapping.class);
                for (Method m : listMethod) {
                    m.setAccessible(true);

                    url = m.getAnnotation(UrlMapping.class).url();
                    methode = m.getAnnotation(UrlMapping.class).method();
                    if (methode != null) {
                        urlMethod = new UrlMethod(url, methode);
                        mapping = new Mapping(className, m);
                        registry.put(urlMethod, mapping);
                    } else {
                        throw new Exception("Methode non defini pour l'url : "  + url);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Méthode commune de traitement des requêtes GET et POST.
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = Utility.getUrlFromRequest(request);
        String methode = request.getMethod();
        PrintWriter out = response.getWriter();

        Object temp = null;
        UrlMethod keyUrlMethod = null;
        Mapping mapping = null;
        HttpMethod meth = null;
        Method method = null;
        try {
            meth = HttpMethod.valueOf(methode);
            if (meth == null) {
                out.println("Methode non gere : " + methode);
            } else {
                keyUrlMethod = new UrlMethod(url, meth);
                mapping = registry.get(keyUrlMethod);

                if (mapping == null) {
                    out.println("Aucun Mapping n'est relie a ceci : " + keyUrlMethod);
                } else {
                    method = mapping.getMethode();
                    Class<?> clazz = Class.forName(mapping.getController());
                    temp = clazz.getConstructor().newInstance();
                    method.invoke(temp);

                    String methodeName = method.getName();
                    String className = mapping.getController();
                    out.println(className + " | " + methodeName);
                }
            }

            out.println("============================ LISTE DES URLMETHODE ==========================");
            for (Map.Entry<UrlMethod, Mapping> map : registry.entrySet()) {
                mapping = map.getValue();
                method = mapping.getMethode();
                Class<?> clazz = Class.forName(mapping.getController());
                temp = clazz.getConstructor().newInstance();
                method.invoke(temp);

                String methodeName = method.getName();
                String className = mapping.getController();
                out.println(className + " | " + methodeName);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}