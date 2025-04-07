package tr.com.kaanhangunay.examples.hotel_management.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import tr.com.kaanhangunay.examples.hotel_management.config.JwtService;
import tr.com.kaanhangunay.examples.hotel_management.dto.request.AuthRequest;
import tr.com.kaanhangunay.examples.hotel_management.dto.response.AuthResponse;
import tr.com.kaanhangunay.examples.hotel_management.dto.response.RefreshTokenResponse;
import tr.com.kaanhangunay.examples.hotel_management.exceptions.JwtInvalidException;
import tr.com.kaanhangunay.examples.hotel_management.exceptions.UserNotFoundException;
import tr.com.kaanhangunay.examples.hotel_management.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;

  public AuthResponse login(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    var jwtId = UUID.randomUUID().toString();
    var user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

    if (user == null) {
      throw new UserNotFoundException(request.getUsername());
    }

    var accessToken = jwtService.generateAccessToken(jwtId, user);
    var refreshToken = jwtService.generateRefreshToken(jwtId, user);
    return new AuthResponse(accessToken, refreshToken);
  }

  public RefreshTokenResponse refreshToken(String refreshToken) {
    String username;
    try {
      username = jwtService.extractUsername(refreshToken);
    } catch (Exception e) {
      throw new JwtInvalidException("Geçersiz refresh token.");
    }

    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    if (!jwtService.isTokenValid(refreshToken, user)) {
      throw new JwtInvalidException("Token geçerli değil veya süresi dolmuş.");
    }

    var jwtId = UUID.randomUUID().toString();
    var newAccessToken = jwtService.generateAccessToken(jwtId, user);
    var newRefreshToken = jwtService.generateRefreshToken(jwtId, user);

    return new RefreshTokenResponse(newAccessToken, newRefreshToken);
  }
}
