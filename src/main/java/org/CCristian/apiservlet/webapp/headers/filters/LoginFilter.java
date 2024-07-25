package org.CCristian.apiservlet.webapp.headers.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.CCristian.apiservlet.webapp.headers.services.LoginService;
import org.CCristian.apiservlet.webapp.headers.services.LoginServiceSessionImpl;

import java.io.IOException;
import java.util.Optional;

@WebFilter({"/carro/*"})
public class LoginFilter implements Filter {    /*Filtro para validar la Sesion de Usuario*/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LoginService service = new LoginServiceSessionImpl();
        Optional<String> username = service.getUsername((HttpServletRequest) request);  /*Se obtiene el nombre de Usuario*/
        if (username.isPresent()){  /*Válida que exista el nombre de Usuario*/
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response)
                    .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lo sentimos no estas autorizado para ingresar a esta pagina!");
        }
    }
}
