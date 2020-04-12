package main.exception;

/**
 * This class is a custom exception. It is thrown if the prolog script fails to be loaded.
 */
public class ConsultException extends Exception {
    @Override
    public String getMessage() {
        return "Failed to consult the script";
    }
}
