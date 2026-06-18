package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import utils.Utility;
import annotation.Controller;

public class FrontServlet extends HttpServlet {
    private List<String> listClass;
    private String packageName;

    public void init() throws ServletException{
        listClass = new ArrayList<>();
        packageName = this.getInitParameter("packageName");
        try {
            listClass.addAll(Utility.getAnnoted(packageName,Controller.class, ElementType.TYPE));
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
        StringBuffer sb = new StringBuffer();
        for(String s : listClass){
            sb.append(s).append(",");
        }
        String url = request.getRequestURI().substring(1);
        PrintWriter out = response.getWriter();
        out.print(url);
        out.println("Liste des classes : " + sb.toString());
    }
}