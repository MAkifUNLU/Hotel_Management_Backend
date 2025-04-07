package tr.com.kaanhangunay.examples.hotel_management.exceptions;

public class JwtInvalidException extends RuntimeException {
  public JwtInvalidException(String message) {
    super(message);
  }
}
