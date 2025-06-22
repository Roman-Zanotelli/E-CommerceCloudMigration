package org.yearup.data.mysql;

import org.yearup.models.Order;

import java.sql.SQLException;

public class InsertOrderExpection extends SQLException {
    public InsertOrderExpection(Order order) {
        super(String.format("""
                An Error Occurred While Inserting OrderLineItem!
                UserID: %d
                CartTotal: %.2f
                """,
                order.getUserId(),
                order.getPrice()
        ));
    }
}
