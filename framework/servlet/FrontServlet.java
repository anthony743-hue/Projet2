package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
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
import helper.Mapping;
import helper.UrlMethod;
import helper.UrlValidator;

public class FrontServlet extends HttpServlet {
    private List<Class<?>> listClass;
    private Map<UrlMethod, Mapping> registry;

    public void init() throws ServletException {
        registry = new HashMap<>();
        String packageName = this.getInitParameter("packageName");
        try {
            listClass = Utility.getAnnotedClasses(Controller.class, Utility.getClassInPackage(packageName));
            List<Method> listMethod = null;
            UrlMethod urlMethod = null;
            Mapping mapping = null;
            String className = null;
            String url = null, methode = null;

            for (Class<?> cl : listClass) {
                className = cl.getName();
                listMethod = Utility.getAnnotedMethod(cl, UrlMapping.class);
                for (Method m : listMethod) {
                    m.setAccessible(true);

                    url = m.getAnnotation(UrlMapping.class).url();
                    methode = m.getAnnotation(UrlMapping.class).method();
                    if (methode.equals("GET") || methode.equals("POST")) {
                        mapping = new Mapping(className, m);
                        urlMethod = new UrlMethod(url, methode);
                        register(urlMethod, mapping);
                    } else {
                        throw new Exception("Methode inconnue : " + methode);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void register(UrlMethod urlMethod, Mapping req) {
        String url = urlMethod.getUrl();
        if (!UrlValidator.isValid(url)) {
            throw new IllegalStateException("Url format invalide");
        }
        if (registry.containsKey(urlMethod)) {
            throw new IllegalStateException("Duplicate mapping detected for URL: " + urlMethod);
        }
        registry.put(urlMethod, req);
    }

    private boolean isPrimitiveType(Object o){
        return true;
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
        String URI = request.getRequestURI().substring(1);
        String methode = request.getMethod();
        String[] parts = URI.split("/");
        String url = "/" + String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
        PrintWriter out = response.getWriter();
        try {
            UrlMethod keyUrlMethod = new UrlMethod(url, methode);
            if (!registry.containsKey(keyUrlMethod)) {
                throw new Exception("REQUETE IMPOSSIBLE : " + keyUrlMethod);
            }
            Mapping mapping = registry.get(keyUrlMethod);
            Method method = mapping.getMethode();

            Class<?> clazz = Class.forName(mapping.getController());
            Object temp = clazz.getConstructor().newInstance(); 
            
            String methodeName = method.getName();
            String className = mapping.getController();
            out.println(className + " || " + methodeName);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}