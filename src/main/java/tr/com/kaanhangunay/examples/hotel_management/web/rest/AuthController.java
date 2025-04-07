package tr.com.kaanhangunay.examples.hotel_management.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.kaanhangunay.examples.hotel_management.config.OttAuthenticationSuccessHandler;
import tr.com.kaanhangunay.examples.hotel_management.dto.request.AuthRequest;
import tr.com.kaanhangunay.examples.hotel_management.dto.request.RefreshTokenRequest;
import tr.com.kaanhangunay.examples.hotel_management.dto.response.AuthResponse;
import tr.com.kaanhangunay.examples.hotel_management.dto.response.RefreshTokenResponse;
import tr.com.kaanhangunay.examples.hotel_management.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<RefreshTokenResponse> refreshToken(
      @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
  }

    /**
     * One time token sisteminde başarılı girişler için oluşturulan
     * OttAuthenticationSuccessHandler sınıfının security ayarlarında
     * /api/v1/auth/ott/login olarak tanımlanmasına rağmen Spring tarafından
     * bu endpoint bir resource gibi işlem yapılmaya çalışılmaktadır.
     * Resource olmaması nedeniyle sürekli olarak konsola hata logları
     * basılmasının önüne geçilmesi için aslında hiç ulaşılamayacak bir
     * endpoint eklenerek resource olmadığı Spring'e bildirilmiştir.
     *
     * @see OttAuthenticationSuccessHandler
     * @return Koda ulaşılamayacağı için herhangi bir dönüş olmaz.
     */
  @PostMapping("/ott/login")
  public ResponseEntity<Void> handleUnnecessaryStaticResourceSearch() {
    return ResponseEntity.ok().build();
  }
}
