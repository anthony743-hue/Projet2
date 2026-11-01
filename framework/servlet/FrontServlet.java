package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import annotation.Auth;
import annotation.RestController;
import constants.HttpCode;
import constants.HttpMethod;
import utils.Messages;
import utils.Responder;
import utils.Utility;
import wrapper.FileWrapper;
import wrapper.Mapping;
import wrapper.ModelView;
import wrapper.MySession;
import wrapper.UrlMethod;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    private Map<UrlMethod, Mapping> registry;
    private Map<String, Object> controllerInstances;

    public void init() throws ServletException {
        registry = (HashMap<UrlMethod, Mapping>) this.getServletContext().getAttribute("mapUrl");
        controllerInstances = new HashMap<>();
        String i18nBase = this.getServletContext().getInitParameter("i18nBase");
        if (i18nBase == null || i18nBase.isBlank()) {
            i18nBase = "messages";
        }
        Messages.init(i18nBase);
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

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String prefix = this.getServletContext().getInitParameter("prefix");
        String suffix = this.getServletContext().getInitParameter("suffix");
        String formatView = prefix + "%s" + suffix;

        String url = Utility.getUrlFromRequest(request);
        HttpMethod meth = HttpMethod.valueOf(request.getMethod());
        UrlMethod keyUrlMethod = new UrlMethod(url, meth);

        Locale locale = request.getLocale();

        try (PrintWriter out = response.getWriter()) {
            if (registry.containsKey(keyUrlMethod)) {
                Mapping mapping = registry.get(keyUrlMethod);
                Method method = mapping.getMethode();

                HttpSession httpSession = request.getSession();

                // --- Vérification d'authentification ---
                if (method.isAnnotationPresent(Auth.class)) {
                    String nameSession = this.getServletContext().getInitParameter("userSession");
                    Object user = httpSession.getAttribute(nameSession);

                    if (user == null) {
                        String msg = Messages.get("auth.unauthorized", locale);
                        if (method.isAnnotationPresent(RestController.class)) {
                            Responder.error(response, HttpCode.UNAUTHORIZED, msg);
                        } else {
                            Responder.text(response, HttpCode.UNAUTHORIZED, msg);
                        }
                        return;
                    }

                    Auth auth = method.getAnnotation(Auth.class);
                    if (auth.roles() != null && auth.roles().length > 0) {
                        String userRole = (String) Utility.getFieldValue(user, "role");
                        boolean hasRole = false;
                        for (String role : auth.roles()) {
                            if (role.equals(userRole)) {
                                hasRole = true;
                                break;
                            }
                        }
                        if (!hasRole) {
                            String msg = Messages.get("auth.forbidden", locale);
                            if (method.isAnnotationPresent(RestController.class)) {
                                Responder.error(response, HttpCode.FORBIDDEN, msg);
                            } else {
                                Responder.text(response, HttpCode.FORBIDDEN, msg);
                            }
                            return;
                        }
                    }
                }

                // 1. Extraction du fichier unique
                FileWrapper uploadedFile = null;
                String contentType = request.getContentType();
                if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                    for (Part part : request.getParts()) {
                        if (part.getSubmittedFileName() != null && part.getSize() > 0) {
                            try (InputStream in = part.getInputStream()) {
                                uploadedFile = FileWrapper.from(in, part.getSubmittedFileName(),
                                        part.getContentType());
                            } finally {
                                part.delete();
                            }
                            break;
                        }
                    }
                }

                // 2. Résolution des paramètres
                Parameter[] parameters = method.getParameters();
                Object[] paramArr = new Object[parameters.length];
                MySession session = null;

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].getType() == FileWrapper.class) {
                        paramArr[i] = uploadedFile;
                    } else if (parameters[i].getType() == MySession.class) {
                        if (session == null) {
                            session = new MySession();
                            Enumeration<String> attributeNames = httpSession.getAttributeNames();
                            while (attributeNames.hasMoreElements()) {
                                String attr = attributeNames.nextElement();
                                session.add(attr, httpSession.getAttribute(attr));
                            }
                        }
                        paramArr[i] = session;
                    } else {
                        paramArr[i] = Utility.resolveParameter(parameters[i], request);
                    }
                }

                // 3. Invocation
                Class<?> clazz = Class.forName(mapping.getController());
                Object controller = controllerInstances.computeIfAbsent(clazz.getName(), k -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                Object retour = method.invoke(controller, paramArr);

                // Synchronisation session
                if (session != null) {
                    for (Map.Entry<String, Object> entry : session.getMap().entrySet()) {
                        if (entry.getValue() == null) {
                            httpSession.removeAttribute(entry.getKey());
                        } else {
                            httpSession.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }
                }

                // 4. Gestion de la réponse
                if (method.isAnnotationPresent(RestController.class)) {
                    response.setContentType("application/json;charset=UTF-8");
                    out.write(Utility.toJson(retour));
                    out.flush();
                } else if (retour instanceof ModelView mv) {
                    for (Entry<String, Object> entry : mv.getAttributes().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    request.getRequestDispatcher(String.format(formatView, mv.getView()))
                            .forward(request, response);
                }
            } else {
                out.println(Messages.get("url.treated", locale, url));
                out.println(Messages.get("url.list.title", locale));
                for (Map.Entry<UrlMethod, Mapping> map : registry.entrySet()) {
                    Mapping m = map.getValue();
                    out.println(m.getController() + " | " + m.getMethode().getName());
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}