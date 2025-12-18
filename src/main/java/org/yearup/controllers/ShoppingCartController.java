package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@CrossOrigin
@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    // Constructor injection for the Dao's
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }


    // each method in this controller requires a Principal object as a parameter
    //Returns the shopping cart for the currently logged-in user
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
            int userId = user.getId();

            // use the shoppingCartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(ResponseStatusException ex){
            throw ex;
        }catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added)

    // Adds a product to the user's cart
    // If the product already exists in the cart, the quantity is incremented
    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@PathVariable int productId, Principal principal){

        try {
            // Identify the logged-in user
            String userName = principal.getName();
            User user =userDao.getByUserName(userName);
            // Validate the product exists before adding it to the cart
            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            int userId = user.getId();

            if (productDao.getById(productId)== null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found");
            // Delegate insert / increment logic to the DAO
            shoppingCartDao.addProduct(userId,productId);
        }
        catch (ResponseStatusException exception){
            throw exception;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops");
        }
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    // Updates the quantity of an existing product in the cart
    // Only the quantity field from ShoppingCartItem is used
    @PutMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item,
                              Principal principal){
        try {
            // Read the desired quantity from the request body
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
            int userId =user.getId();
            int quantity = item.getQuantity();
            // Update the cart item for this user and product
            shoppingCartDao.updateProductQuantity(userId,productId,quantity);
        }catch (ResponseStatusException exception){
            throw exception;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops our bad");
        }
    }
    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

    // Removes all products from the current user's shopping cart
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
            int userId =user.getId();
            shoppingCartDao.clearCart(userId);
        }catch (ResponseStatusException exception){
            throw exception;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops our bad");
        }
    }

}
