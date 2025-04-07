package tr.com.kaanhangunay.examples.hotel_management.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.kaanhangunay.examples.hotel_management.entity.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
  List<Authority> findByNameIn(List<String> roleNames);
}
