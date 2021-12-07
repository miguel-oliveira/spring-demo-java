package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class MyService {

  public static final Logger LOGGER = LoggerFactory.getLogger(MyService.class);

  public static final String BEAN_NAME = "MyService";

  private final MyRepository repository;
  private final EntityManager entityManager;
  private final SnapshotService snapshotService;
  private final MyContextHolder myContextHolder;

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

  @SuppressWarnings("unchecked")
  public List<MyEntity> getLatestRevisionUntil(Instant modifiedAt) {
    return AuditReaderFactory
        .get(entityManager)
        .createQuery()
        .forRevisionsOfEntity(MyEntity.class, true, false)
        .add(
            AuditEntity.property("modifiedAt").maximize().computeAggregationInInstanceContext()
                .add(AuditEntity.property("modifiedAt").le(modifiedAt)))
        .getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<MyEntity> getAllRevisions() {
    return AuditReaderFactory
        .get(entityManager)
        .createQuery()
        .forRevisionsOfEntity(MyEntity.class, true, false)
        .getResultList();
  }

  public MyEntity create(MyEntity entity) {
    entity.setId(null);
    return repository.save(entity);
  }

  public MyEntity update(String id, MyEntity entity) {
    final Optional<MyEntity> saved = repository.findById(id);
    if (saved.isPresent()) {
      entity.setId(id);
      entity.setVersion(saved.get().getVersion());
      return repository.save(entity);
    } else {
      throw new EntityNotFoundException(id);
    }
  }

  public void delete(String id) {
    final Optional<MyEntity> saved = repository.findById(id);
    if (saved.isPresent()) {
      repository.deleteById(id);
    } else {
      throw new EntityNotFoundException(id);
    }
  }

  public void snapshot(Long time) {
    try {
      snapshotService.snapshot(time);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Async
  public void asyncContextTest() {
    LOGGER.info("Async context: {}", myContextHolder.getUsername());
  }
}
