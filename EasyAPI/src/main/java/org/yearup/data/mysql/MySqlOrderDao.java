package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.exception.InsertException;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order checkOut(Profile userData, ShoppingCart cart) {

        //Try Connection
        try(Connection connection = getConnection()){
            //Create Base Order
            Order order = new Order(userData, cart);
            //Disable AutoCommit to do Rollbacks if something fails
            connection.setAutoCommit(false);
            try {

                //INSERT Orders Table
                {
                    //Prepare Statement
                    PreparedStatement insertOrder = connection.prepareStatement("""
                            INSERT INTO `orders` (`user_id`, `date`, `address`, `city`, `state`, `zip`, `shipping_amount`)
                            VALUES (?, ?, ?, ?, ?, ?, ?);
                            """, Statement.RETURN_GENERATED_KEYS);

                    //Fill Values
                    insertOrder.setInt(1, order.getUserId());
                    insertOrder.setDate(2, order.getDate());
                    insertOrder.setString(3, order.getAddress());
                    insertOrder.setString(4, order.getCity());
                    insertOrder.setString(5, order.getState());
                    insertOrder.setString(6, order.getZip());
                    insertOrder.setBigDecimal(7, order.getPrice());

                    //Throw if nothing changes
                    if(insertOrder.executeUpdate() == 0) throw new InsertException(order, "No Rows Changed While Creating Order");

                    //Get Generated Keys
                    ResultSet res = insertOrder.getGeneratedKeys();

                    //Throw if there isn't a generated key
                    if(!res.next()) throw new InsertException(order, "No Keys Generated While Creating Order");

                    //Update the orderID to the generated value
                    order.setOrderId(res.getInt(1));
                }


                //INSERT OrderLineItems table
                {
                    //List of line items inserted
                    ArrayList<OrderLineItem> lineItems = new ArrayList<>();

                    //Loop through every item in the cart
                    for(OrderLineItem lineItem : order.convertLineItems()){
                        //Prepare Statement
                        PreparedStatement insertOrderLineItem = connection.prepareStatement("""
                                INSERT INTO `order_line_items` (`order_id`, `product_id`, `sales_price`, `quantity`, `discount`)
                                VALUES(?, ?, ?, ?, ?);
                                """, Statement.RETURN_GENERATED_KEYS);

                        //Fill values
                        insertOrderLineItem.setInt(1, lineItem.getOrderId());
                        insertOrderLineItem.setInt(2, lineItem.getProductId());
                        insertOrderLineItem.setBigDecimal(3, lineItem.getSalesPrice());
                        insertOrderLineItem.setInt(4, lineItem.getQuantity());
                        insertOrderLineItem.setBigDecimal(5, lineItem.getDiscount());

                        //Execute and Throw if nothing changes
                        if(insertOrderLineItem.executeUpdate() == 0) throw new InsertException(lineItem, "No Rows Changed While Creating OrderLineItem");

                        //Get generated keys
                        ResultSet res = insertOrderLineItem.getGeneratedKeys();

                        //Throw if unavailable
                        if(!res.next()) throw new InsertException(lineItem, "No Keys Generated While Creating OrderLineItem");

                        //Update the ID with the generated value
                        lineItem.setOrderLineItemId(res.getInt(1));

                        //Add to list of items
                        lineItems.add(lineItem);
                    }

                    //store the inserted line items
                    order.setLineItems(lineItems);
                }

                //Commit Changes
                connection.commit();

                //Return final order
                return order;

            }catch (SQLException e){

                //Roll Back if anything happens
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
