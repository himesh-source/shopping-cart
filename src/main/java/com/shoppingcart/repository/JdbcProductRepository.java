package com.shoppingcart.repository;

import com.shoppingcart.domain.CatalogProduct;

import javax.sql.DataSource;

public class JdbcProductRepository implements ProductRepository {
    private final DataSource dataSource;

    public JdbcProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CatalogProduct findByName(String name) {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(
                     "SELECT name, price FROM product WHERE name = ?")) {

            ps.setString(1, name);

            var rs = ps.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Product not found: " + name);
            }

            return new CatalogProduct(
                    rs.getString("name"),
                    rs.getBigDecimal("price")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
