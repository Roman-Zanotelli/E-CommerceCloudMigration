package org.yearup.data.mysql;

import org.yearup.models.OrderLineItem;

import java.sql.SQLException;

public class InsertOrderLineItemExpection extends SQLException {
    public InsertOrderLineItemExpection(OrderLineItem lineItem) {
        super(String.format("""
                An Error Occurred While Inserting OrderLineItem!
                OrderID: %d
                ProductID: %d
                SalesPrice: %.2f
                Discount: %.2f
                """,
                lineItem.getOrderId(),
                lineItem.getProductId(),
                lineItem.getSalesPrice(),
                lineItem.getDiscount()
                ));
    }
}
