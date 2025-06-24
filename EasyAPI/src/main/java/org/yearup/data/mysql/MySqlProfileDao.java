package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.exception.InsertException;
import org.yearup.data.exception.UpdateException;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{

    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        //Query String
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //Try Connection
        try(Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //Fill Values
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            //Throw if nothing changes
            if(ps.executeUpdate() == 0) throw new InsertException(profile, "No Rows Changed After Creation");

            //return the profile
            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int id) {

        //Query String
        String sql = "SELECT * FROM profiles WHERE user_id = ?;";

        //Try Connection
        try(Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //Fill Value
            ps.setInt(1, id);

            //Execute Query
            ResultSet result = ps.executeQuery();

            //Return the result or null
            return result.next() ? new Profile(result): null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile update(int id, Profile profile) {
        //Query String
        String sql = """
                UPDATE `profiles`
                SET
                `first_name` = ?,
                `last_name` = ?,
                `phone` = ?,
                `email` = ?,
                `address` = ?,
                `city` = ?,
                `state` = ?,
                `zip` = ?
                WHERE `user_id` = ?;
                """;

        //Try Connection
        try(Connection connection = getConnection())
        {
            //Prepare Statement
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //Fill Values
            ps.setString(1, profile.getFirstName());
            ps.setString(2, profile.getLastName());
            ps.setString(3, profile.getPhone());
            ps.setString(4, profile.getEmail());
            ps.setString(5, profile.getAddress());
            ps.setString(6, profile.getCity());
            ps.setString(7, profile.getState());
            ps.setString(8, profile.getZip());
            ps.setInt(9, id);

            //Throw If nothing changes
            if(ps.executeUpdate() == 0) throw new UpdateException(id, profile, "No Rows Changed After Profile Update");

            //return the current entry of the users profile
            return getByUserId(id);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

}
