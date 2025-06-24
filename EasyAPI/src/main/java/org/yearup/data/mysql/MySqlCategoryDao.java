package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.data.exception.DeleteException;
import org.yearup.data.exception.InsertException;
import org.yearup.data.exception.UpdateException;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        //List to hold categories
        List<Category> categories = new ArrayList<>();

        //Query String
        String sql = "SELECT * FROM categories;";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Execute
            ResultSet row = statement.executeQuery();

            //Map and Add Each row into categories
            while (row.next()) categories.add(mapRow(row));

            //return list
            return categories;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Category getById(int categoryId)
    {
        //Query String
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        //Try Connection
        try (Connection connection = getConnection()) {

            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, categoryId);

            //Execute
            ResultSet row = statement.executeQuery();

            //Return Result or Null
            return row.next() ? mapRow(row) : null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category create(Category category)
    {

        //Query String
        String sqlQuery = "INSERT INTO categories(category_id, name, description) VALUES (?, ?, ?);";

        //Try Connection with resources
        try (Connection connection = getConnection())
        {
            //Disable Auto-Commit for Rollbacks
            connection.setAutoCommit(false);
            try {

                //Prepare Statement
                PreparedStatement statement = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);

                //Fill Values
                statement.setInt(1, category.getCategoryId());
                statement.setString(2, category.getName());
                statement.setString(3, category.getDescription());


                //Throw if nothing changed
                if (statement.executeUpdate() == 0) throw new InsertException(category, "No Rows Changed While Creating Category");

                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                //Throw if there is no key
                if (!generatedKeys.next()) throw new InsertException(category, "No Keys Generated While Creating Category");

                //Commit actions
                connection.commit();

                // get the newly inserted category
                return getById(generatedKeys.getInt(1));

            } catch (InsertException e){
                //Rollback actions
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        //Query String
        String sql = "UPDATE categories SET category_id = ? , name = ? , description = ? WHERE category_id = ?;";

        //Try connection with resources
        try (Connection connection = getConnection())
        {
            //Prepare statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill values
            statement.setInt(1, category.getCategoryId());
            statement.setString(2, category.getName());
            statement.setString(3, category.getDescription());
            statement.setInt(4, categoryId);

            //Throw if nothing changes
            if(statement.executeUpdate() == 0) throw new UpdateException(categoryId, category, "No Rows Changed While Updating Category");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        //Query String
        String sql = "DELETE FROM categories WHERE category_id = ?;";

        //Try connection with resources
        try (Connection connection = getConnection())
        {
            //Prepare statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Values
            statement.setInt(1, categoryId);

            //Throw If Nothing Changed
            if(statement.executeUpdate() == 0) throw new DeleteException(categoryId, "No Rows Changed While Deleting Category");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        return new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};
    }

}
