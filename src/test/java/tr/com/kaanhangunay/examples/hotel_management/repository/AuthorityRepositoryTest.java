package tr.com.kaanhangunay.examples.hotel_management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tr.com.kaanhangunay.examples.hotel_management.entity.Authority;

@DataJpaTest
@ActiveProfiles("test")
class AuthorityRepositoryTest {

  @Autowired private AuthorityRepository authorityRepository;

  @BeforeEach
  void setUp() {
    authorityRepository.saveAll(
        List.of(
            new Authority(null, "ROLE_MANAGER"),
            new Authority(null, "ROLE_EMPLOYEE"),
            new Authority(null, "ROLE_GUEST")));
  }

  @Test
  @DisplayName("Should find authorities by list of role names")
  void shouldFindAuthoritiesByRoleNames() {
    List<String> roleNames = List.of("ROLE_MANAGER", "ROLE_GUEST");
    List<Authority> found = authorityRepository.findByNameIn(roleNames);

    assertThat(found).hasSize(2);
    assertThat(found)
        .extracting(Authority::getName)
        .containsExactlyInAnyOrder("ROLE_MANAGER", "ROLE_GUEST");
  }

  @Test
  @DisplayName("Should return empty list when roles not exist")
  void shouldReturnEmptyWhenNoMatchingRoles() {
    List<String> roleNames = List.of("ROLE_SUPER_ADMIN", "ROLE_UNKNOWN");
    List<Authority> found = authorityRepository.findByNameIn(roleNames);

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should return all when all roles match")
  void shouldReturnAllWhenAllRolesMatch() {
    List<String> roleNames = List.of("ROLE_MANAGER", "ROLE_EMPLOYEE", "ROLE_GUEST");
    List<Authority> found = authorityRepository.findByNameIn(roleNames);

    assertThat(found).hasSize(3);
  }
}
