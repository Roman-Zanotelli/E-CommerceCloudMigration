package org.yearup.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Order {
    private int orderId;
    private Profile userData;
    private Date date;
    private ShoppingCart cart;
    private ArrayList<OrderLineItem> lineItems;
    public Order(Profile user, ShoppingCart cart) throws SQLException{
        this.userData = user;
        this.cart = cart;
        this.date = Date.valueOf(LocalDate.now());
    }
    public ShoppingCart getCart(){
        return cart;
    }


    public ArrayList<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userData.getUserId();
    }


    public Date getDate() {
        return date;
    }


    public String getAddress() {
        return userData.getAddress();
    }


    public String getCity() {
        return userData.getCity();
    }



    public String getState() {
        return userData.getState();
    }


    public String getZip() {
        return userData.getZip();
    }


    public BigDecimal getPrice() {
        return cart.getTotal();
    }

}
