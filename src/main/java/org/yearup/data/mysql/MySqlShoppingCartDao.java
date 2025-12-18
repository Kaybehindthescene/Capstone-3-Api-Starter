package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource){

        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId){
        String sql = "SELECT p.product_id, p.name, p.price, p.category_id, p.description, p.subcategory, p.stock, p.featured, p.image_url, " +
                "       sci.quantity " +
                "FROM shopping_cart_items sci " +
                "JOIN products p ON p.product_id = sci.product_id " +
                "WHERE sci.user_id = ?;";
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql){
                preparedStatement.setInt(1,userId);

                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    while (resultSet.next()){
                        Product product = mapProductRow(resultSet);       // build Product from the joined columns
                        int qty = resultSet.getInt("quantity");

                        ShoppingCartItem item = new ShoppingCartItem();
                        item.setProduct(product);
                        item.setQuantity(qty);

                        items.put(product.getProductId(), item);
                    }
                    }
                }
                catch (SQLException e) {
                    throw new RuntimeException(e);
        }
        }

    }
}
