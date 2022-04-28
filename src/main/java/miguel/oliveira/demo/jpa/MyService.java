package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.jpa.dto.MyEntityQueryParams;
import miguel.oliveira.demo.jpa.dto.MyEntityUpdateRequest;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyService {

  private final MyRepository repository;
  private final EntityManager entityManager;
  private final SnapshotService snapshotService;
  private final SpecificationBuilder specificationBuilder;

  public Page<MyEntity> getAll(MyEntityQueryParams queryParams, Pageable pageable) {
    final Specification<MyEntity> specification = buildSpecification(queryParams);
    return repository.findAll(specification, pageable);
  }

  private Specification<MyEntity> buildSpecification(MyEntityQueryParams queryParams) {
    Stream<Specification<MyEntity>> specificationStream =
        Stream.of(
            specificationBuilder.likeSpecification(x -> x.get(MyEntity_.NAME),
                queryParams.getName()),
            specificationBuilder.instantEqualSpecification(x -> x.get(MyEntity_.CREATED_AT),
                queryParams.getCreatedAt()),
            specificationBuilder.instantSpecification(x -> x.get(MyEntity_.MODIFIED_AT),
                queryParams.getModifiedAtFrom(),
                queryParams.getModifiedAtTo()),
            specificationBuilder.instantSpecification(x -> x.get(MyEntity_.CREATED_AT),
                queryParams.getCreatedAtFrom(),
                queryParams.getCreatedAtTo()),
            specificationBuilder.inSpecification(x -> x.get(MyEntity_.NAME),
                queryParams.getNameIn())
        );
    return specificationStream
        .filter(Objects::nonNull)
        .reduce(Specification::and)
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

  public MyEntity update(MyEntityUpdateRequest updateRequest) {
    final Optional<MyEntity> saved = repository.findById(updateRequest.getId());
    if (saved.isPresent()) {
      final MyEntity persisted = saved.get();
      persisted.setName(updateRequest.getName());
      return repository.save(persisted);
    } else {
      throw new EntityNotFoundException(updateRequest.getId());
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

}
