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
