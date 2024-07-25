package org.CCristian.apiservlet.webapp.headers.listeners;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.CCristian.apiservlet.webapp.headers.models.Carro;

@WebListener
public class AplicacionListener implements ServletContextListener,
        ServletRequestListener, HttpSessionListener {

    private ServletContext servletContext;

    /*ServletContextListener*/
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().log("Inicializando la Aplicación");
        servletContext = sce.getServletContext();
        servletContext.setAttribute("mensaje","Algún valor global de la App!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext.log("Destruyendo la Aplicación");
    }


    /*ServletRequestListener*/
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        servletContext.log("Inicializando el Request!");
        sre.getServletRequest().setAttribute("mensaje","Guardando algún para el Request");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        servletContext.log("Destruyendo el Request!");
    }


    /*HttpSessionListener*/
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        servletContext.log("Creando la Sesion Http!");
        Carro carro = new Carro();
        HttpSession session = se.getSession();
        session.setAttribute("carro", carro);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        servletContext.log("Destruyendo la Sesion Http!");
    }
}
