package miguel.oliveira.demo.mongodb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.integration.transformer.ObjectToMapTransformer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonService {

  private final MongoTemplate mongoTemplate;
  private final PersonRepository personRepository;

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

  public void export() {
    export(personRepository.findAll());
  }

  @SneakyThrows
  private void export(final List<Person> persons) {
    ObjectToMapTransformer transformer = new ObjectToMapTransformer();
    transformer.setShouldFlattenKeys(true);
    List<Map<String, Object>> flattened = new LinkedList<>();
    for (Person person : persons) {
      flattened.add(
          (Map<String, Object>) transformer.transform(new GenericMessage<>(person)).getPayload()
      );
    }
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(flattened);
    JsonNode jsonTree = new ObjectMapper().readTree(json);
    Builder csvSchemaBuilder = CsvSchema.builder();
    JsonNode firstObject = jsonTree.elements().next();
    firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
    CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
    CsvMapper csvMapper = new CsvMapper();
    csvMapper
        .writerFor(JsonNode.class)
        .with(csvSchema)
        .writeValue(new File("persons.csv"), jsonTree);
  }
}
