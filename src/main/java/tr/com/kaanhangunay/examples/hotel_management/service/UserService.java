package tr.com.kaanhangunay.examples.hotel_management.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tr.com.kaanhangunay.examples.hotel_management.entity.Authority;
import tr.com.kaanhangunay.examples.hotel_management.entity.User;
import tr.com.kaanhangunay.examples.hotel_management.repository.AuthorityRepository;
import tr.com.kaanhangunay.examples.hotel_management.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthorityRepository authorityRepository;

  public User registerUser(String username, String password, List<String> roleNames) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));

    List<Authority> authorities = authorityRepository.findByNameIn(roleNames);
    user.setAuthorities(authorities);

    return userRepository.save(user);
  }
}
