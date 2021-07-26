package miguel.oliveira.demo.mongodb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
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
    final String filename = String.format("%s/%s.csv", "miguel", UUID.randomUUID());
    final List<Person> persons = new ArrayList<>(personRepository.findAll());
    IntStream
        .range(0, persons.size())
        .forEach(i -> {
          final List<Person> ps = new LinkedList<>();
          ps.add(persons.get(i));
          export(exportRequest, ps, filename, i == 0);
        });
  }

  @SneakyThrows
  private void export(ExportRequest exportRequest, List<Person> personList, String filename,
      boolean withHeader) {
    exportService.export(exportRequest, personList, filename, withHeader);
  }

}
