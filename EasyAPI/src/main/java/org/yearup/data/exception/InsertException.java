package org.yearup.data.exception;

import org.yearup.models.Category;

import java.sql.SQLException;

public class InsertException extends SQLException {
    public InsertException(Object target, String message) {
        super(String.format("""
                An Error Occurred While Creating!
                %s
                %s
                """,
                message,
                target.toString()
                ));
    }
}
