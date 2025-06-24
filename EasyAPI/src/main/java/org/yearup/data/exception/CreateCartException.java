package org.yearup.data.exception;

import java.sql.SQLException;

public class CreateCartException extends SQLException {
    public CreateCartException(int userId, int productId, String message) {
        super(String.format("""
                An Error Occurred While Creating Cart Item!
                %s
                User ID: %d
                Product ID: %d
                """,
                message,
                userId,
                productId
                ));
    }
}
