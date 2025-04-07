package tr.com.kaanhangunay.examples.hotel_management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tr.com.kaanhangunay.examples.hotel_management.entity.Authority;
import tr.com.kaanhangunay.examples.hotel_management.entity.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private AuthorityRepository authorityRepository;

  @Autowired private EntityManager entityManager;

  private Authority roleEmployee;

  @BeforeEach
  void setUp() {
    Authority roleManager = authorityRepository.save(new Authority(null, "ROLE_MANAGER"));
    roleEmployee = authorityRepository.save(new Authority(null, "ROLE_EMPLOYEE"));

    User user = new User();
    user.setUsername("kaanhangunay");
    user.setPassword("encoded-pass");
    user.setAuthorities(List.of(roleManager, roleEmployee));

    userRepository.save(user);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("Should find user by username when it exists")
  void shouldFindUserByUsername() {
    Optional<User> result = userRepository.findByUsername("kaanhangunay");
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("kaanhangunay");
    assertThat(result.get().getAuthorities()).hasSize(2);
  }

  @Test
  @DisplayName("Should return empty when username does not exist")
  void shouldReturnEmptyIfUsernameNotExists() {
    Optional<User> result = userRepository.findByUsername("nonexistent");
    assertThat(result).isNotPresent();
  }

  @Test
  @DisplayName("Should save new user and retrieve")
  void shouldSaveAndRetrieveUser() {
    User newUser = new User();
    newUser.setUsername("newuser");
    newUser.setPassword("newpass");
    newUser.setAuthorities(List.of(roleEmployee));
    userRepository.save(newUser);

    Optional<User> saved = userRepository.findByUsername("newuser");
    assertThat(saved).isPresent();
    assertThat(saved.get().getAuthorities()).hasSize(1);
    assertThat(saved.get().getAuthorities().get(0).getAuthority()).isEqualTo("ROLE_EMPLOYEE");
  }
}
