package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {

        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = "SELECT p.product_id, p.name, p.price, p.category_id, p.description, p.subcategory, p.stock, p.featured, p.image_url, " +
                "       sci.quantity " +
                "FROM shopping_cart_items sci " +
                "JOIN products p ON p.product_id = sci.product_id " +
                "WHERE sci.user_id = ?;";
        Map<Integer, ShoppingCartItem> items = new HashMap<>();


        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapProductRow(resultSet);
                    int qty = resultSet.getInt("quantity");

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(qty);

                    items.put(product.getProductId(), item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        return cart;
    }

    private Product mapProductRow(ResultSet resultSet) throws SQLException{

        int productId = resultSet.getInt("Product_Id");
        String name = resultSet.getString("Name");
        BigDecimal price = resultSet.getBigDecimal("Price");
        int categoryId = resultSet.getInt("Category_Id");
        String description = resultSet.getString("Description");
        String subCategory = resultSet.getString("SubCategory");
        int stock = resultSet.getInt("Stock");
        boolean featured = resultSet.getBoolean("Featured");
        String imageUrl = resultSet.getString("Image_Url");

        return new Product(productId, name, price, categoryId, description, subCategory, stock, featured, imageUrl);
    }

    @Override
    public void addProduct(int userId, int productId){
        String sql = "INSERT INTO shopping_cart_items (user_id, product_id, quantity) " +
                "VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,productId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateProductQuantity(int userId, int productId, int quantity){
        if (quantity <= 0){
            removeProduct(userId,productId);
            return;
        }
        String sql = "UPDATE shopping_cart_items " +
                "SET quantity = ? " +
                "WHERE user_id = ? AND product_id = ?;";
        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,productId);
            preparedStatement.setInt(3,quantity);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void removeProduct(int userId, int productId){
        String sql = "DELETE FROM shopping_cart_items " +
                "WHERE user_id = ? AND product_id = ?;";
        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,productId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void clearCart(int userId){
        String sql = "DELETE FROM shopping_cart_items WHERE user_id = ?;";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
