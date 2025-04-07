package tr.com.kaanhangunay.examples.hotel_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authorities")
public class Authority {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_id_seq")
  @SequenceGenerator(
      name = "authority_id_seq",
      sequenceName = "authority_id_seq",
      allocationSize = 1)
  private Long id;

  private String name;
}
