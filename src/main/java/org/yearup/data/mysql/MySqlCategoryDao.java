package org.yearup.data.mysql;

import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.yearup.data.mysql.MySqlProductDao.mapRow;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        //done
        String sql = """
                Select *
                From categories
                order by category_id
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                ){
            List<Category> categories = new ArrayList<>();
            while (resultSet.next()){
                categories.add(mapRow(resultSet));
            }
            return categories;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        //done
       String sql = """
               SELECT category_id, name, description 
               FROM categories 
               WHERE category_id = ?
               """;
       try(
               Connection connection = getConnection();
               PreparedStatement preparedStatement = connection.prepareStatement(sql)
               ) {
           preparedStatement.setInt(1, categoryId);

           try (ResultSet resultSet = preparedStatement.executeQuery()) {
               if (resultSet.next()) {
                   return mapRow(resultSet);
               }
           }
           } catch (SQLException e) {
               throw new RuntimeException(e);
           }
       return null;

    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        //done
        String sql = """
                INSERT INTO categories (name, description) VALUES (?, ?)
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ){
            preparedStatement.setString(1,category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();

            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()){
                return getById(keys.getInt(1));
            }
            return null;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        //done
        String sql = """
                UPDATE categories SET name = ?, description = ? WHERE category_id = ?
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ){
            preparedStatement.setString(1,category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3,categoryId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        //done
        String sql = """
                Delete
                From categories
                Where category_id = ?
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ){
            preparedStatement.setInt(1,categoryId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
