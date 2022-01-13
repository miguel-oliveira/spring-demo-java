package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

@Service
@AllArgsConstructor
public class MyService {

  public static final Logger LOGGER = LoggerFactory.getLogger(MyService.class);

  private static final char LIKE_ESCAPE_CHAR = '!';
  private static final String LIKE_ESCAPE_STRING = "!";

  public static final String BEAN_NAME = "MyService";

  private final MyRepository repository;
  private final EntityManager entityManager;
  private final SnapshotService snapshotService;
  private final MyContextHolder myContextHolder;

  public Page<MyEntity> getAll(MyEntityQueryParams queryParams, Pageable pageable) {
    final Specification<MyEntity> specification = buildSpecification(queryParams);
    return repository.findAll(specification, pageable);
  }

  private <T> Specification<T> buildSpecification(MyEntityQueryParams queryParams) {
    Stream<Specification<T>> specificationStream =
        Stream.of(
            likeSpecification(x -> x.get(MyEntity_.NAME), queryParams.getName()),
            instantEqualSpecification(x -> x.get(MyEntity_.CREATED_AT), queryParams.getCreatedAt()),
            instantSpecification(x -> x.get(MyEntity_.MODIFIED_AT), queryParams.getModifiedAtFrom(),
                queryParams.getModifiedAtTo()),
            instantSpecification(x -> x.get(MyEntity_.CREATED_AT), queryParams.getCreatedAtFrom(),
                queryParams.getCreatedAtTo())
        );
    return specificationStream
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse(Specification.where(null));
  }

  private <T> Specification<T> likeSpecification(
      Function<Root<T>, Expression<String>> expression,
      String value) {
    return likeSpecification((root, cb) -> expression.apply(root), value);
  }

  private <T> Specification<T> likeSpecification(
      BiFunction<Root<T>, CriteriaBuilder, Expression<String>> expression,
      String value) {
    if (value != null) {
      return (root, query, cb) -> likePredicate(cb, expression.apply(root, cb), value);
    }
    return Specification.where(null);
  }

  private Predicate likePredicate(CriteriaBuilder cb, Expression<String> expression, String value) {
    Expression<String> lowerExpression = cb.lower(expression);

    boolean requiresEscape = value.contains("%") || value.contains("_");
    if (requiresEscape) {
      String escapedValue = value
          .replace(LIKE_ESCAPE_STRING, LIKE_ESCAPE_CHAR + LIKE_ESCAPE_STRING)
          .replace("%", LIKE_ESCAPE_CHAR + "%")
          .replace("_", LIKE_ESCAPE_CHAR + "_");
      String likeExpression = "%".concat(escapedValue.toLowerCase(Locale.getDefault())).concat("%");
      return cb.like(lowerExpression, likeExpression, LIKE_ESCAPE_CHAR);
    } else {
      String likeExpression = "%".concat(value.toLowerCase(Locale.getDefault())).concat("%");
      return cb.like(lowerExpression, likeExpression);
    }
  }

  private <T> Specification<T> instantEqualSpecification(
      Function<Root<T>, Expression<Instant>> expression,
      Long timestamp
  ) {
    if (timestamp != null) {
      return (root, query, cb) -> cb.equal(expression.apply(root), Instant.ofEpochMilli(timestamp));
    }
    return Specification.where(null);
  }

  private <T> Specification<T> instantSpecification(
      Function<Root<T>, Expression<Instant>> expression,
      Long valueFrom,
      Long valueTo) {
    if (Objects.nonNull(valueFrom) && Objects.nonNull(valueTo)) {
      return (root, query, cb) -> cb
          .between(expression.apply(root), Instant.ofEpochMilli(valueFrom),
              Instant.ofEpochMilli(valueTo));
    } else if (Objects.nonNull(valueFrom)) {
      return (root, query, cb) -> cb
          .greaterThanOrEqualTo(expression.apply(root), Instant.ofEpochMilli(valueFrom));
    } else if (Objects.nonNull(valueTo)) {
      return (root, query, cb) -> cb
          .lessThanOrEqualTo(expression.apply(root), Instant.ofEpochMilli(valueTo));
    }
    return null;
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
