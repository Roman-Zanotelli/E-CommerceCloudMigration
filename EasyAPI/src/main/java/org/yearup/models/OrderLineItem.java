package org.yearup.models;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderLineItem{

    int orderLineItemId, orderId , productId;
    BigDecimal salesPrice, discount;
    int quantity;

    public OrderLineItem( int orderId, int productId, BigDecimal salesPrice, BigDecimal discount, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.salesPrice = salesPrice;
        this.discount = discount;
        this.quantity = quantity;
    }

    public int getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(int orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
