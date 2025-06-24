package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.exception.CreateCartException;
import org.yearup.data.exception.UpdateCartException;
import org.yearup.data.exception.UpdateException;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        //Empty Cart to hold values
        ShoppingCart cart = new ShoppingCart();

        //Query String
        String sql = "SELECT * FROM shopping_cart JOIN products ON shopping_cart.product_id = products.product_id WHERE user_id = ?;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, userId);

            //Execute Query
            ResultSet row = statement.executeQuery();

            //Map and insert each row into the cart as a cartItem
            while (row.next()) cart.add(new ShoppingCartItem(row));
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return cart;
    }

    @Override
    public ShoppingCart createCart(int userId, int productId) {
        //Query String
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES(?, ?, 1) ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            //Throw if nothing changed
            if(statement.executeUpdate() == 0) throw new CreateCartException(userId, productId, "No Rows Changed After Insert/Increment");

            //Get the current cart
            return getByUserId(userId);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart updateCart(int userId, int productId, int quantity) {
        //Query String
        String sql = " UPDATE shopping_cart SET user_id = ?, product_id = ?, quantity = ? WHERE user_id = ? AND product_id = ?;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.setInt(4, userId);
            statement.setInt(5, productId);

            //Throw If Nothing Changed
            if(statement.executeUpdate() == 0) throw new UpdateCartException(userId, productId, quantity, "No Rows Changed After Update");

            //Return Current Cart
            return getByUserId(userId);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart deleteCart(int userId) {
        //Query String
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Value
            statement.setInt(1, userId);
            statement.executeUpdate();
            //Return if anything changed
            return  getByUserId(userId);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


}
