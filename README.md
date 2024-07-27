<h1 align="center">Clase ConexionBaseDatos</h1>

```java
public class ConexionBaseDatos {

    /*----PARÁMETROS DE LA BASE DE DATOS----*/
    private static String url = "jdbc:mysql://localhost:3306/java_curso?serverTimezone=America/Argentina/Buenos_Aires";
    private static String username = "root";
    private static String password = "sasa";

    /*----MÉTODO CONEXIÓN A LA BASE DE DATOS----*/
    public static Connection getConection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
```

Esta clase se encarga de gestionar la conexión a la base de datos.

- Atributos:
  - `url`: URL de la base de datos.
  - `username`: Nombre de usuario de la base de datos.
  - `password`: Contraseña de la base de datos.
- Método:
  - `getConection()`: Este método establece y devuelve una conexión a la base de datos utilizando `DriverManager.getConnection` con los parámetros de URL, nombre de usuario y contraseña.

<h1 align="center">Clase ConexionFilter</h1>

```java
package org.CCristian.apiservlet.webapp.headers.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.CCristian.apiservlet.webapp.headers.services.ServiceJdbcException;
import org.CCristian.apiservlet.webapp.headers.util.ConexionBaseDatos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebFilter("/*")
public class ConexionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("No se pudo cargar el controlador JDBC", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try (Connection conn = ConexionBaseDatos.getConection()){
            if (conn.getAutoCommit()){
                conn.setAutoCommit(false);
            }
            try {
                request.setAttribute("conn", conn);
                chain.doFilter(request, response);
                conn.commit();
            } catch (SQLException | ServiceJdbcException e){
                conn.rollback();
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Esta clase es un filtro servlet que gestiona las conexiones a la base de datos para cada solicitud.

- Métodos:
  - `init(FilterConfig filterConfig)`: Este método se ejecuta al inicializar el filtro y carga el controlador JDBC.
  - `doFilter(ServletRequest request, ServletResponse response, FilterChain chain)`: Este método se ejecuta para cada solicitud entrante. Establece una conexión a la base de datos, desactiva el auto-commit, y añade la conexión como un atributo de la solicitud. Luego, permite que la solicitud continúe a través del filtro. Si ocurre alguna excepción, se realiza un rollback de la transacción y se envía un error HTTP 500.

<h1 align="center">ProductoRepositoryJdbcImpl</h1>

```java
package org.CCristian.apiservlet.webapp.headers.repositories;

import org.CCristian.apiservlet.webapp.headers.models.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryJdbcImpl implements Repository<Producto>{

    private Connection conn;

    public ProductoRepositoryJdbcImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.*, c.nombre as categoria FROM productos AS p " +
                                     " INNER JOIN categorias AS c ON (p.categoria_id = c.id)" +
                                  " ORDER BY p.id")){
            while (rs.next()){
                Producto p = getProducto(rs);
                productos.add(p);
            }
        }
        return productos;
    }


    @Override
    public Producto porId(Long id) throws SQLException {
        Producto producto = null;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT p.*, c.nombre AS categoria FROM productos AS p "+
                " INNER JOIN categorias AS c ON (p.categoria_id = c.id) WHERE p.id = ?")){
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    producto = getProducto(rs);
                }
            }
        }
        return producto;
    }

    @Override
    public void guardar(Producto producto) throws SQLException {

    }

    @Override
    public void eliminar(Long id) throws SQLException {

    }

    private static Producto getProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getLong("id"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getInt("precio"));
        p.setTipo(rs.getString("categoria"));
        return p;
    }
}
```

Esta clase implementa la interfaz `Repository<Producto>` y proporciona métodos para interactuar con la base de datos.

- Atributo:
  - `conn`: La conexión a la base de datos.
- Métodos:
  - `listar()`: Este método devuelve una lista de todos los productos en la base de datos. Ejecuta una consulta SQL que une las tablas de productos y categorías y devuelve los resultados como objetos `Producto`.
  - `porId(Long id)`: Este método devuelve un producto específico por su ID. Ejecuta una consulta SQL que une las tablas de productos y categorías donde el ID del producto coincide con el ID proporcionado.
  - `getProducto(ResultSet rs)`: Este método es un helper que crea un objeto `Producto` a partir de un `ResultSet`.

<h1 align="center">ProductosServiceJdbcImpl</h1>

```java
package org.CCristian.apiservlet.webapp.headers.services;

import org.CCristian.apiservlet.webapp.headers.models.Producto;
import org.CCristian.apiservlet.webapp.headers.repositories.ProductoRepositoryJdbcImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductosServiceJdbcImpl implements ProductoService{

    private ProductoRepositoryJdbcImpl repositoryJdbc;

    public ProductosServiceJdbcImpl(Connection connection) {
        this.repositoryJdbc = new ProductoRepositoryJdbcImpl(connection);
    }

    @Override
    public List<Producto> listar(){
        try {
            return repositoryJdbc.listar();
        } catch (SQLException throwables) {
            throw new ServiceJdbcException(throwables.getMessage(), throwables.getCause());
        }
    }

    @Override
    public Optional<Producto> porId(Long id) {
        try {
            return Optional.ofNullable(repositoryJdbc.porId(id));
        } catch (SQLException throwables) {
            throw new ServiceJdbcException(throwables.getMessage(), throwables.getCause());
        }
    }
}
```

Esta clase implementa la interfaz `ProductoService` y proporciona lógica de negocio para gestionar productos.

- Atributo:
  - `repositoryJdbc`: Una instancia de `ProductoRepositoryJdbcImpl` para interactuar con la base de datos.
- Métodos:
  - `listar()`: Este método llama al método `listar` del repositorio y devuelve una lista de productos. Si ocurre una excepción, se lanza una `ServiceJdbcException`.
  - `porId(Long id)`: Este método llama al método `porId` del repositorio y devuelve un producto por su ID. Si ocurre una excepción, se lanza una `ServiceJdbcException`.

 <h1 align="center"></h1>

```java
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
```

Este servlet maneja las solicitudes HTTP para listar productos.

- Método:
  - `doGet(HttpServletRequest req, HttpServletResponse resp)`: Este método maneja las solicitudes GET. Obtiene una conexión a la base de datos de la solicitud, crea una instancia de `ProductosServiceJdbcImpl` y obtiene una lista de productos. También obtiene el nombre de usuario del `LoginService`, añade los productos y el nombre de usuario como atributos de la solicitud, y reenvía la solicitud al JSP `listar.jsp`.
 
