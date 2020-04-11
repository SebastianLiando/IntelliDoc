package core;

public class ConsultException extends Exception {
    @Override
    public String getMessage() {
        return "Failed to consult the script";
    }
}
