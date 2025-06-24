package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.exception.DeleteException;
import org.yearup.data.exception.InsertException;
import org.yearup.data.exception.UpdateException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{

    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
    {
        //List to store products found
        List<Product> products = new ArrayList<>();

        //Query String
        String sql = "SELECT * FROM products " +
                //Category ID
                "WHERE (category_id = ? OR ? = -1) " +
                //Min Price Bound
                "   AND (price >= ? OR ? = -1) " +
                //Max Price Bound
                "   AND (price <= ? OR ? = -1) " +
                //Color
                "   AND (color = ? OR ? = '') ";

        //Defaulting null values
        categoryId = (categoryId == null ? -1 : categoryId);
        minPrice = (minPrice == null ? new BigDecimal("-1") : minPrice);
        maxPrice = (maxPrice == null ? new BigDecimal("-1") : maxPrice);
        color = (color == null ? "" : color);

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            //category_id
            statement.setInt(1, categoryId);
            statement.setInt(2, categoryId);
            //min price bound
            statement.setBigDecimal(3, minPrice);
            statement.setBigDecimal(4, minPrice);
            //max price bound
            statement.setBigDecimal(5, maxPrice);
            statement.setBigDecimal(6, maxPrice);
            //color
            statement.setString(7, color);
            statement.setString(8, color);

            //Execute query
            ResultSet row = statement.executeQuery();

            //Map and Add Each row to products
            while (row.next()) products.add(mapRow(row));

            //Return list of found
            return products;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        //List of products found
        List<Product> products = new ArrayList<>();

        //Query String
        String sql = "SELECT * FROM products WHERE category_id = ?;";

        //Try connection with resources
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Value
            statement.setInt(1, categoryId);

            //Execute
            ResultSet row = statement.executeQuery();

            //Map and Add Rows to products list
            while (row.next()) products.add(mapRow(row));
            //Return list
            return products;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Product getById(int productId)
    {
        //Query String
        String sql = "SELECT * FROM products WHERE product_id = ?";

        //Try Connection with Resources
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, productId);

            //Execute Query
            ResultSet row = statement.executeQuery();

            //map a value if it exists or return null
            return row.next() ? mapRow(row): null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product create(Product product)
    {
        //Query String
        String sql = "INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        //Try Connection with Resources
        try (Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);
                //Prepare Statement
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                //Fill Values
                statement.setString(1, product.getName());
                statement.setBigDecimal(2, product.getPrice());
                statement.setInt(3, product.getCategoryId());
                statement.setString(4, product.getDescription());
                statement.setString(5, product.getColor());
                statement.setString(6, product.getImageUrl());
                statement.setInt(7, product.getStock());
                statement.setBoolean(8, product.isFeatured());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) throw new InsertException(product, "Zero Rows Affected By Insert");
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (!generatedKeys.next()) throw new InsertException(product, "No Valid Generated Key By Insert");

                //Commit Actions
                connection.commit();

                // Retrieve the auto-incremented ID
                int orderId = generatedKeys.getInt(1);

                // get the newly inserted category
                return getById(orderId);


            } catch(InsertException e){
                //Rollback Actions
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int productId, Product product)
    {
        //Query String
        String sql = """
                UPDATE products
                SET name = ? , price = ? , category_id = ? , description = ? , color = ? , image_url = ? , stock = ? , featured = ?
                WHERE product_id = ?;
                """;

        //Try Connection with Resources
        try (Connection connection = getConnection())
        {
            // Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            //Throw if nothing changes
            if(statement.executeUpdate() == 0) throw new UpdateException(productId, product, "No Rows Changed After Update");
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int productId)
    {
        //Query String
        String sql = "DELETE FROM products WHERE product_id = ?;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, productId);

            //Throw if nothing changed
            if(statement.executeUpdate() == 0) throw new DeleteException(productId, "No Rows Changed After Deletion");
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}
