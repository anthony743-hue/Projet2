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
        registry = (HashMap) this.getServletContext().getAttribute("mapUrl");
        listClass = (List<Class<?>>) this.getServletContext().getAttribute("listClass");
       
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