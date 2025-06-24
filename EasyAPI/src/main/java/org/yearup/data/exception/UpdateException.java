package org.yearup.data.exception;

import org.yearup.models.Category;

import java.sql.SQLException;

public class UpdateException extends SQLException {
    public UpdateException(int id, Object target, String message) {
        super(String.format("""
                An Error Occurred While Updating!
                %s
                Target ID: %d
                %s
                """,
                message,
                id,
                target.toString()
                ));
    }
}
