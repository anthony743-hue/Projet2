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

import utils.Utility;
import annotation.Controller;
import annotation.UrlMapping;
import helper.RequestHandler;
import helper.UrlValidator;

public class FrontServlet extends HttpServlet {
    private Map<String, RequestHandler> registry;

    public void init() throws ServletException{
        registry = new ConcurrentHashMap<>();
        String packageName = this.getInitParameter("packageName");
        try {
            List<Object> listClass = Utility.getAnnoted(packageName,Controller.class);
            List<Object> listReq = null;
            for(Object obj : listClass){
                if( obj instanceof Class<?> cl ){
                    listReq = Utility.getAnnotedMethod(cl,UrlMapping.class);
                    for(Object o : listReq){
                        if( o instanceof RequestHandler req ){
                            register(req);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void register(RequestHandler req) {
        String url = req.getTargetMethod().getAnnotation(UrlMapping.class).url();
        if(!UrlValidator.isValid(url)){
            throw new IllegalStateException("Url format invalide");
        }
        if (registry.containsKey(url)) {
            throw new IllegalStateException("Duplicate mapping detected for URL: " + url);
        }
        registry.put(url,req);
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
        String[] parts = URI.split("/");
        String url = "/" + String.join("/", Arrays.copyOfRange(parts,1,parts.length));
        PrintWriter out = response.getWriter();
        try {
            if(!registry.containsKey(url)){
                throw new Exception("Url inconnu : " + url);
            }
            out.println(url);
            RequestHandler req = registry.get(url);
            String methodeName = req.getTargetMethod().getName();
            String className = req.getControllerInstance().getClass().getSimpleName();
            out.println(className + " || " + methodeName);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}