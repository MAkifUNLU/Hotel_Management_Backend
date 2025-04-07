package tr.com.kaanhangunay.examples.hotel_management.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String username) {
    super("Kullanıcı bulunamadı: " + username);
  }
}
