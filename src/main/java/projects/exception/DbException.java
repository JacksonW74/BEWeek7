package projects.exception;

/**
 * Custom exception class for database-related errors.
 */
@SuppressWarnings("serial")
public class DbException extends RuntimeException {

    /**
     * Constructs a new database exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DbException(String message) {
        super(message);
    }

    /**
     * Constructs a new database exception with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DbException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new database exception with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}
