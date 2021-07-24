package miguel.oliveira.demo.mongodb;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonService {

  private final MongoTemplate mongoTemplate;
  private final PersonRepository personRepository;
  private final ExportService exportService;

  public String create(Person person) {
    return personRepository.save(person).getId();
  }

  public Optional<Person> update(String id, Person update) {
    final Person person = mongoTemplate
        .findOne(Query.query(Criteria.where("id").is(id)), Person.class);

    if (person != null) {
      person.setName(update.getName());
      person.setAge(update.getAge());
      return Optional.of(personRepository.save(person));
    } else {
      return Optional.empty();
    }
  }

  public Page<Person> get(Pageable pageable) {
    return personRepository.findAll(pageable);
  }

  public Optional<Person> get(String id) {
    return personRepository.findById(id);
  }

  @SneakyThrows
  public void export(ExportRequest exportRequest) {
    exportService.export(exportRequest, personRepository.findAll());
  }

}
