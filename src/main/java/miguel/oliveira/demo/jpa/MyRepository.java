package miguel.oliveira.demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRepository
    extends JpaRepository<MyEntity, String>, JpaSpecificationExecutor<MyEntity> {

  MyEntity findByCodeAndNamespace(String code, String namespace);
}
