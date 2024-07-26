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

<h2>Método getConection():</h2>

- Este método es `public` y `static`, lo que significa que puede ser llamado sin necesidad de instanciar un objeto de la clase `ConexionBaseDatos`.
- Devuelve un objeto de tipo `Connection`, que es una conexión abierta a la base de datos.
- Utiliza el método `DriverManager.getConnection(url, username, password)` para crear y retornar una conexión a la base de datos MySQL usando los parámetros definidos anteriormente.
- Si ocurre un error al intentar conectarse, este método lanzará una excepción `SQLException`.

<h2>Flujo de Trabajo</h2>

- Cuando llames a `ConexionBaseDatos.getConection()`, el método:
  - Utilizará los parámetros `url`, `username` y `password` para intentar establecer una conexión con la base de datos MySQL.
  - Si la conexión es exitosa, retornará un objeto `Connection`.
  - Si ocurre algún problema (por ejemplo, si la base de datos no está accesible, el usuario o contraseña son incorrectos, etc.), lanzará una excepción `SQLException`.

<h1 align="center">Clase ConexionFilter</h1>

```java
package org.CCristian.apiservlet.webapp.headers.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.CCristian.apiservlet.webapp.headers.util.ConexionBaseDatos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebFilter("/*")
public class ConexionFilter implements Filter {
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
            } catch (SQLException e){
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

<h2>Explicación</h2>

- Anotación `@WebFilter`:
  - `@WebFilter("/*")` indica que este filtro se aplicará a todas las solicitudes (`/*`) que lleguen al servidor.
- Método `doFilter`:
  - Este es el método principal del filtro, que intercepta las solicitudes y respuestas.
- Establecimiento de la Conexión:
  - `try (Connection conn = ConexionBaseDatos.getConection())`: Intenta obtener una conexión a la base de datos usando el método `getConection()` de la clase `ConexionBaseDatos`. El bloque `try-with-resources` asegura que la conexión se cerrará automáticamente al final del bloque, incluso si ocurre una excepción.
- Configuración de `AutoCommit`:
  - `if (conn.getAutoCommit()) { conn.setAutoCommit(false); }`: Verifica si la conexión tiene `auto-commit` activado y lo desactiva si es necesario. Esto significa que las transacciones no se confirmarán automáticamente después de cada operación SQL, sino que se controlarán manualmente.
- Paso de la Conexión a la Solicitud:
  - `request.setAttribute("conn", conn);`: La conexión se añade como un atributo a la solicitud para que esté disponible durante el procesamiento de la solicitud.
  - `chain.doFilter(request, response);`: Se pasa la solicitud y la respuesta al siguiente filtro o servlet en la cadena de filtros.
- Confirmación y Control de Transacciones:
  - `conn.commit();`: Si no hay excepciones, la transacción se confirma (commit) después de que se complete el procesamiento de la solicitud.
  - `catch (SQLException e) { conn.rollback(); ... }`: Si ocurre una excepción `SQLException` durante el procesamiento de la solicitud, se deshacen (rollback) todas las operaciones de la transacción. También se envía un error HTTP 500 (Internal Server Error) al cliente con el mensaje de la excepción y se imprime la pila de llamadas.
- Manejo de Excepciones al Conectar:
  - `catch (SQLException e) { throw new RuntimeException(e); }`: Si ocurre una excepción `SQLException` al obtener la conexión a la base de datos, se envuelve en una `RuntimeException` y se lanza. Esto detiene el procesamiento de la solicitud y propaga el error hacia arriba.

<h2>Flujo de Trabajo Completo</h2>

- Interceptación de la Solicitud:
    - Cada solicitud entrante es interceptada por el `ConexionFilter`.
- Conexión a la Base de Datos:
  - Se obtiene una conexión a la base de datos.
  - Se desactiva el `auto-commit` para manejar manualmente las transacciones.
- Procesamiento de la Solicitud:
  - La conexión se agrega como un atributo de la solicitud.
  - La solicitud se pasa al siguiente filtro o servlet.
- Confirmación o Reversión de la Transacción:
  - Si el procesamiento de la solicitud es exitoso, se confirma la transacción.
  - Si ocurre una excepción, se deshacen las operaciones y se envía un error al cliente.

<p>Este filtro asegura que cada solicitud tenga su propia conexión a la base de datos y que las transacciones sean manejadas adecuadamente, asegurando la integridad de los datos en caso de errores.</p>
