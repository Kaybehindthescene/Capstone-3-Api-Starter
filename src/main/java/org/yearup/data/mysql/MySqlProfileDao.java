package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

        @Override
        public Profile create(Profile profile)
        {
            String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql))
            {
                preparedStatement.setInt(1, profile.getUserId());
                preparedStatement.setString(2, profile.getFirstName());
                preparedStatement.setString(3, profile.getLastName());
                preparedStatement.setString(4, profile.getPhone());
                preparedStatement.setString(5, profile.getEmail());
                preparedStatement.setString(6, profile.getAddress());
                preparedStatement.setString(7, profile.getCity());
                preparedStatement.setString(8, profile.getState());
                preparedStatement.setString(9, profile.getZip());

                preparedStatement.executeUpdate();
                return profile;
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Profile getByUserId(int userId)
        {
            String sql = "SELECT user_id, first_name, last_name, phone, email, address, city, state, zip " +
                    "FROM profiles WHERE user_id = ?";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql))
            {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery())
                {
                    if (resultSet.next())
                        return mapRow(resultSet);

                    return null;
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void update(int userId, Profile profile)
        {
            String sql = "UPDATE profiles " +
                    "SET first_name = ?, last_name = ?, phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ? " +
                    "WHERE user_id = ?";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql))
            {
                preparedStatement.setString(1, profile.getFirstName());
                preparedStatement.setString(2, profile.getLastName());
                preparedStatement.setString(3, profile.getPhone());
                preparedStatement.setString(4, profile.getEmail());
                preparedStatement.setString(5, profile.getAddress());
                preparedStatement.setString(6, profile.getCity());
                preparedStatement.setString(7, profile.getState());
                preparedStatement.setString(8, profile.getZip());
                preparedStatement.setInt(9, userId);

                preparedStatement.executeUpdate();
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        private Profile mapRow(ResultSet resultSet) throws SQLException
        {
            Profile profile = new Profile();
            profile.setUserId(resultSet.getInt("user_id"));
            profile.setFirstName(resultSet.getString("first_name"));
            profile.setLastName(resultSet.getString("last_name"));
            profile.setPhone(resultSet.getString("phone"));
            profile.setEmail(resultSet.getString("email"));
            profile.setAddress(resultSet.getString("address"));
            profile.setCity(resultSet.getString("city"));
            profile.setState(resultSet.getString("state"));
            profile.setZip(resultSet.getString("zip"));
            return profile;
        }
    }
