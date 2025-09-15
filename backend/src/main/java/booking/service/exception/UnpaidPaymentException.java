package booking.service.exception;

public class UnpaidPaymentException extends RuntimeException {
    
    public UnpaidPaymentException(String message) {
        super(message);
    }
    
    public UnpaidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
} 
