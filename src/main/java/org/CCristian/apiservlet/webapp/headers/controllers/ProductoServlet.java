package org.CCristian.apiservlet.webapp.headers.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.CCristian.apiservlet.webapp.headers.models.Producto;
import org.CCristian.apiservlet.webapp.headers.services.LoginService;
import org.CCristian.apiservlet.webapp.headers.services.LoginServiceSessionImpl;
import org.CCristian.apiservlet.webapp.headers.services.ProductoService;
import org.CCristian.apiservlet.webapp.headers.services.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet({"/productos.html", "/productos"})
public class ProductoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection conn = (Connection) req.getAttribute("conn"); /*Obtiene la conexión a la Base de Datos*/
        ProductoService service = new ProductosServiceJdbcImpl(conn);

        List<Producto> productos = null;   /*Obtiene una lista con los Productos*/
        try {
            productos = service.listar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LoginService auth = new LoginServiceSessionImpl();
        Optional<String> usernameOptional = auth.getUsername(req);  /*Obtiene en nombre de Usuario*/

        /*Pasando parámetros*/
        req.setAttribute("productos",productos);
        req.setAttribute("username",usernameOptional);
        getServletContext().getRequestDispatcher("/listar.jsp").forward(req, resp);
    }
}
