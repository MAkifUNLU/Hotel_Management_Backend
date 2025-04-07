package tr.com.kaanhangunay.examples.hotel_management.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final Environment env;
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final OttAuthenticationSuccessHandler ottAuthenticationSuccessHandler;
  private final OttAuthenticationFailureHandler ottAuthenticationFailureHandler;
  private final OttTokenGenerationSuccessHandler ottTokenGenerationSuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    List<String> profiles = Arrays.asList(env.getActiveProfiles());
    boolean isDevProfile = !profiles.contains("prod");

    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(
            headers -> {
              if (isDevProfile) {
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
              }
            })
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers("/api/v1/auth/**").permitAll();

              if (isDevProfile) {
                auth.requestMatchers("/h2-console/**", "/favicon.ico").permitAll();
              }

              auth.anyRequest().authenticated();
            })
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .oneTimeTokenLogin(
            ott -> {
              ott.loginProcessingUrl("/api/v1/auth/ott/login");
              ott.tokenGeneratingUrl("/api/v1/auth/ott/generate");
              ott.authenticationSuccessHandler(ottAuthenticationSuccessHandler);
              ott.authenticationFailureHandler(ottAuthenticationFailureHandler);
              ott.tokenGenerationSuccessHandler(ottTokenGenerationSuccessHandler);
            })
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
