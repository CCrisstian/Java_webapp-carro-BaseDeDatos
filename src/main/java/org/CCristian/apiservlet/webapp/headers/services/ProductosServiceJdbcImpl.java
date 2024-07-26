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