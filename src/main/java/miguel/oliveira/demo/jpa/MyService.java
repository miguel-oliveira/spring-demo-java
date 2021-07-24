package miguel.oliveira.demo.jpa;

import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class MyService {

  private final MyRepository repository;

  public Page<MyEntity> getAll(MyEntityQueryParams queryParams, Pageable pageable) {
    final Specification<MyEntity> specification =
        likeSpecification(queryParams.getName(), MyEntity_.name);
    return repository.findAll(specification, pageable);
  }

  private <X> Specification<X> likeSpecification(
      String searchParam,
      SingularAttribute<X, String> column) {
    return Optional.ofNullable(searchParam)
        .filter(StringUtils::hasText)
        .map(s ->
            (Specification<X>) (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get(column)),
                    String.format("%%%s%%", s.toLowerCase())))
        .orElse(Specification.where(null));
  }

  public MyEntity create(MyEntity entity) {
    return repository.save(entity);
  }
}
