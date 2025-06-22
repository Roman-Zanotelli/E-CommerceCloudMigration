package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String sql = "SELECT * FROM shopping_cart JOIN products ON shopping_cart.product_id = products.product_id WHERE user_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet row = statement.executeQuery();
            while (row.next()) cart.add(new ShoppingCartItem(row));
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return cart;
    }

    @Override
    public ShoppingCart addCart(int userId, int productId) {
        String sql =
                "INSERT INTO shopping_cart (user_id, product_id, quantity)" +
                "VALUES(?, ?, 1)" +
                "ON CONFLICT (user_id, product_id)" +
                "DO UPDATE SET quantity = shopping_cart.quantity + 1;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return getByUserId(userId);
    }

    @Override
    public ShoppingCart updateCart(int userId, int productId, int quantity) {
        String sql = " UPDATE shopping_cart" +
                        "SET user_id = ?, product_id = ?, quantity = ?" +
                        "WHERE user_id = ? AND product_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.setInt(4, userId);
            statement.setInt(5, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return getByUserId(userId);
    }

    @Override
    public boolean deleteCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            return statement.executeUpdate() != 0;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


}
