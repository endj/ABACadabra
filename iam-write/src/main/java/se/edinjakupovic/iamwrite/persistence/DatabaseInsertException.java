package se.edinjakupovic.iamwrite.persistence;

public class DatabaseInsertException extends RuntimeException {
    public DatabaseInsertException(String message) {
        super(message);
    }
}
