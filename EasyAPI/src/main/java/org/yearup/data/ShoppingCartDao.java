package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addCart(int userId, int itemId);
    ShoppingCart updateCart(int userId, int itemId, int amount);
    boolean deleteCart(int userID);
}
