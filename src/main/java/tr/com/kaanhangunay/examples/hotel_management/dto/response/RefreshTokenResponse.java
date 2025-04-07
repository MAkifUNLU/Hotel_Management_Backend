package tr.com.kaanhangunay.examples.hotel_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
  private String accessToken;
  private String refreshToken;
}
