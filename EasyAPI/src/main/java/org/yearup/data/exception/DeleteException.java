package org.yearup.data.exception;

import java.sql.SQLException;

public class DeleteException extends SQLException {
    public DeleteException(int id, String message) {
        super(String.format("""
                An Error Occurred While Deleting!
                %s
                Target ID: %d
                """,
                message,
                id
                ));
    }
}
