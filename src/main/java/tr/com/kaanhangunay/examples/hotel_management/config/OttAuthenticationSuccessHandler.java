package tr.com.kaanhangunay.examples.hotel_management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tr.com.kaanhangunay.examples.hotel_management.dto.response.AuthResponse;
import tr.com.kaanhangunay.examples.hotel_management.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class OttAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    User user = (User) authentication.getPrincipal();
    var jwtId = UUID.randomUUID().toString();
    var accessToken = jwtService.generateAccessToken(jwtId, user);
    var refreshToken = jwtService.generateRefreshToken(jwtId, user);
    AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    response.getWriter().flush();
    response.flushBuffer();
  }
}
