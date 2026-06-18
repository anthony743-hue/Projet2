package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;

public class FrontServlet extends HttpServlet {

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
        String url = request.getRequestURI().substring(1);
        PrintWriter out = response.getWriter();
        out.print(url);
    }
}