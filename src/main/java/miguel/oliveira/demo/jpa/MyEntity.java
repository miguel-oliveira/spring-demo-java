package miguel.oliveira.demo.jpa;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Audited
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "compositeKey", columnNames = {"code", "namespace"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class MyEntity {

  @Id
  @GenericGenerator(
      name = "string-sequence-generator",
      strategy = "miguel.oliveira.demo.jpa.generator.StringSequenceGenerator",
      parameters = {
          @Parameter(name = "sequence_name", value = "hibernate_sequence"),
          @Parameter(name = "sequence_prefix", value = "MY_ID_PREFIX_")
      })
  @GeneratedValue(generator = "string-sequence-generator", strategy = GenerationType.SEQUENCE)
  private String id;

  @NotBlank
  @Column(nullable = false, updatable = false)
  private String code;

  @NotBlank
  @Column(nullable = false, updatable = false)
  private String namespace;

  @Version
  private long version;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @CreatedDate
  @Column(updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  private Instant modifiedAt;

  @CreatedDate
  @Column(updatable = false)
  private Instant actionDate;

  @NotBlank
  private String name;

}
