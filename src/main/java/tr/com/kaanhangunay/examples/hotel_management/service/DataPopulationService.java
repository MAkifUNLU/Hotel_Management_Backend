package tr.com.kaanhangunay.examples.hotel_management.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tr.com.kaanhangunay.examples.hotel_management.entity.Authority;
import tr.com.kaanhangunay.examples.hotel_management.entity.User;
import tr.com.kaanhangunay.examples.hotel_management.repository.AuthorityRepository;
import tr.com.kaanhangunay.examples.hotel_management.repository.UserRepository;

@Service
@Profile("!prod")
@RequiredArgsConstructor
public class DataPopulationService implements ApplicationListener<ApplicationReadyEvent> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthorityRepository authorityRepository;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    Authority managerAuth = new Authority();
    managerAuth.setName("ROLE_MANAGER");
    authorityRepository.save(managerAuth);

    Authority employeeAuth = new Authority();
    employeeAuth.setName("ROLE_EMPLOYEE");
    authorityRepository.save(employeeAuth);

    Authority guestAuth = new Authority();
    guestAuth.setName("ROLE_GUEST");
    authorityRepository.save(guestAuth);

    User manager = new User();
    manager.setUsername("manager");
    manager.setPassword(passwordEncoder.encode("pass"));
    manager.setAuthorities(List.of(managerAuth, employeeAuth));
    userRepository.save(manager);

    User employee = new User();
    employee.setUsername("employee");
    employee.setPassword(passwordEncoder.encode("pass"));
    employee.setAuthorities(List.of(employeeAuth));
    userRepository.save(employee);

    User guest = new User();
    guest.setUsername("guest");
    guest.setPassword(passwordEncoder.encode("pass"));
    guest.setAuthorities(List.of(guestAuth));
    userRepository.save(guest);
  }
}
