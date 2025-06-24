package org.yearup.data.exception;

import java.sql.SQLException;

public class UpdateCartException extends SQLException {
    public UpdateCartException(int userId, int productId, int quantity, String message) {
        super(String.format("""
                An Error Occurred While Updating Cart Item!
                %s
                User ID: %d
                Product ID: %d
                Quantity: %d
                """,
                message,
                userId,
                productId,
                quantity
                ));
    }
}
