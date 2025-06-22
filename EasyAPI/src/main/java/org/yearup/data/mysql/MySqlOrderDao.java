package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    @Autowired
    ShoppingCartDao cartDao;

    @Override
    public Order checkOut(Profile userData) {
        try(Connection connection = getConnection()){
            Order order = new Order(userData, cartDao.getByUserId(userData.getUserId()));
            //Disable AutoCommit to do Rollbacks if something fails
            connection.setAutoCommit(false);
            try {
                //Orders Table
                {
                    PreparedStatement insertOrder = connection.prepareStatement("""
                            INSERT INTO `orders`
                            (
                            `user_id`,
                            `date`,
                            `address`,
                            `city`,
                            `state`,
                            `zip`,
                            `shipping_amount`)
                            VALUES
                            (
                            ?,
                            ?,
                            ?,
                            ?,
                            ?,
                            ?,
                            ?);
                            """, Statement.RETURN_GENERATED_KEYS);
                    insertOrder.setInt(1, order.getUserId());
                    insertOrder.setDate(2, order.getDate());
                    insertOrder.setString(3, order.getAddress());
                    insertOrder.setString(4, order.getCity());
                    insertOrder.setString(5, order.getState());
                    insertOrder.setString(6, order.getZip());
                    insertOrder.setBigDecimal(7, order.getPrice());

                    insertOrder.executeUpdate();
                    ResultSet res = insertOrder.getGeneratedKeys();
                    if(!res.next()) throw new InsertOrderExpection(order);
                    order.setOrderId(res.getInt("order_id"));
                }
                //INSERT OrderLineItems table
                {
                    ArrayList<OrderLineItem> lineItems = new ArrayList<>();
                    for(ShoppingCartItem cartItem : order.getCart().getItems().values()){
                        OrderLineItem lineItem = cartItem.convertToLineItem(order.getOrderId());
                        PreparedStatement insertOrderLineItem = connection.prepareStatement("""
                                INSERT INTO `order_line_items`
                                (
                                `order_id`,
                                `product_id`,
                                `sales_price`,
                                `quantity`,
                                `discount`)
                                VALUES
                                (
                                ?,
                                ?,
                                ?,
                                ?,
                                ?);
                                """, Statement.RETURN_GENERATED_KEYS);
                        insertOrderLineItem.setInt(1, lineItem.getOrderId());
                        insertOrderLineItem.setInt(2, lineItem.getProductId());
                        insertOrderLineItem.setBigDecimal(3, lineItem.getSalesPrice());
                        insertOrderLineItem.setInt(4, lineItem.getQuantity());
                        insertOrderLineItem.setBigDecimal(5, lineItem.getDiscount());
                        if(insertOrderLineItem.executeUpdate() == 0) throw new InsertOrderLineItemExpection(lineItem);
                        ResultSet res = insertOrderLineItem.getGeneratedKeys();
                        if(!res.next()) throw new InsertOrderLineItemExpection(lineItem);
                        lineItem.setOrderLineItemId(res.getInt("order_line_item_id"));
                        lineItems.add(lineItem);
                    }
                    order.setLineItems(lineItems);
                }

                //Commit Changes
                connection.commit();
                return order;
            }catch (Exception e){
                //Roll Back if anything happens
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
