package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.data.exception.InsertException;
import org.yearup.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlUserDao extends MySqlDaoBase implements UserDao
{
    public MySqlUserDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public User create(User newUser)
    {
        //Query String
        String sql = "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)";

        //Hash Password
        String hashedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //Fill Values
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());

            //Throw If Nothing Changed
            if(ps.executeUpdate() == 0) throw new InsertException(newUser, "No Rows Changed After User Creation");

            //Get Current User Entry
            User user = getByUserName(newUser.getUsername());

            //Clear Password
            user.setPassword("");

            //Return User Entry
            return user;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll()
    {
        //List to hold users
        List<User> users = new ArrayList<>();

        //Query String
        String sql = "SELECT * FROM users";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Execute Query
            ResultSet row = statement.executeQuery();

            //Map and Add each row to users
            while (row.next()) users.add(mapRow(row));

            //return users
            return users;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserById(int id)
    {
        //Query String
        String sql = "SELECT * FROM users WHERE user_id = ?";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Value
            statement.setInt(1, id);

            //Execute Query
            ResultSet row = statement.executeQuery();

            //Return result or null
            return row.next() ? mapRow(row) : null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getByUserName(String username)
    {
        //Query String
        String sql = "SELECT * FROM users  WHERE username = ?";

        //Try Connection
        try (Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement statement = connection.prepareStatement(sql);

            //Fill Value
            statement.setString(1, username);

            //Execute Query
            ResultSet row = statement.executeQuery();

            //Return Result or null
            return row.next() ? mapRow(row) : null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getIdByUsername(String username)
    {
        User user = getByUserName(username);
        return user != null ? user.getId() : -1;
    }

    @Override
    public boolean exists(String username)
    {
        User user = getByUserName(username);
        return user != null;
    }

    private User mapRow(ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");
        return new User(userId, username,hashedPassword, role);
    }
}
