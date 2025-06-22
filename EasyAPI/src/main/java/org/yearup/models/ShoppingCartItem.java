package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingCartItem
{
    private Product product = null;
    private int quantity = 1;
    private BigDecimal discountPercent = BigDecimal.ZERO;

    public ShoppingCartItem(ResultSet row) throws SQLException {
        this.product = new Product(row);
        this.quantity = row.getInt("quantity");
    }


    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    @JsonIgnore
    public int getProductId()
    {
        return this.product.getProductId();
    }

    public BigDecimal getLineTotal()
    {
        BigDecimal basePrice = product.getPrice();
        BigDecimal quantity = new BigDecimal(this.quantity);

        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }

    public OrderLineItem convertToLineItem(int orderId){
        return new OrderLineItem(orderId, this.product.getProductId(), this.product.getPrice(), this.discountPercent, this.quantity);
    }
}
