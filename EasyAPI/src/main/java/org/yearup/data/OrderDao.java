package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

public interface OrderDao {
    Order checkOut(Profile userData, ShoppingCart cart);
}
