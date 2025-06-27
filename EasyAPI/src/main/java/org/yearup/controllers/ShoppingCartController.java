package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.observe.Metrics;

import java.security.Principal;


@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    @Autowired
    private ShoppingCartDao shoppingCartDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private Metrics metrics;



    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            metrics.increment("api_get_cart");
            // get the currently logged in username
            String userName = principal.getName();

            // find database user by userId
            User user = userDao.getByUserName(userName);

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(user.getId());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping("products/{id}")
    public ShoppingCart addCart(@PathVariable int id, Principal principal)
    {
        try
        {
            metrics.increment("api_add_cart");
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            return shoppingCartDao.createCart(user.getId(), id);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("products/{id}")
    public ShoppingCart updateCart(@PathVariable int id, Principal principal, @RequestBody ShoppingCartItem cartItem)
    {
        try
        {
            metrics.increment("api_update_cart");
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            return shoppingCartDao.updateCart(user.getId(), id, cartItem.getQuantity());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping
    public ShoppingCart deleteCart(Principal principal)
    {
        try
        {
            metrics.increment("api_delete_cart");
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            return shoppingCartDao.deleteCart(user.getId());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
