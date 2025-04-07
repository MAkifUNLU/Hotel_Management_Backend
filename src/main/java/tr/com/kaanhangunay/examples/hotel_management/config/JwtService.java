package tr.com.kaanhangunay.examples.hotel_management.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, String claimName, Class<T> returnType) {
    Claims claims = extractAllClaims(token);
    return claims.get(claimName, returnType);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public <USER extends UserDetails> boolean isTokenValid(String token, USER user) {
    String username = extractUsername(token);
    return username.equals(user.getUsername()) && !isTokenExpired(token);
  }

  public <USER extends UserDetails> String generateAccessToken(String jwtId, USER user) {
    Map<String, Object> claims = new HashMap<>();

    List<String> authorities =
        user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    claims.put("authorities", authorities);

    return Jwts.builder()
        .id(jwtId)
        .claims(claims)
        .subject(user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
        .signWith(getSignKey())
        .compact();
  }

  public <USER extends UserDetails> String generateRefreshToken(String jwtId, USER user) {
    return Jwts.builder()
        .id(jwtId)
        .subject(user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
        .signWith(getSignKey())
        .compact();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignKey() {
    byte[] key = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(key);
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }
}
