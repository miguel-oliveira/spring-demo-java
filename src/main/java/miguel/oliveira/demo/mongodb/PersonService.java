package miguel.oliveira.demo.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import miguel.oliveira.demo.mongodb.ExportRequest.Field;
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

  public void export(ExportRequest exportRequest) {
    export(exportRequest, personRepository.findAll());
  }

  @SneakyThrows
  private void export(final ExportRequest exportRequest, final List<Person> persons) {
    final List<Map<String, Object>> flattened = compute(exportRequest, persons);
    final JsonNode jsonTree = convertToJson(flattened);
    writeToCsv(jsonTree);
  }

  private List<Map<String, Object>> compute(
      final ExportRequest exportRequest,
      final List<Person> persons) {
    final ObjectToMapTransformer transformer = new ObjectToMapTransformer();
    transformer.setShouldFlattenKeys(true);
    final List<Map<String, Object>> flattened = new LinkedList<>();
    for (Person person : persons) {
      final Map<String, Object> personMap =
          (Map<String, Object>) transformer.transform(new GenericMessage<>(person)).getPayload();
      final Map<String, Object> selectedFields = new HashMap<>();
      for (Field field : exportRequest.getFields()) {
        selectedFields.put(field.getName(), personMap.getOrDefault(field.getPath(), ""));
      }
      flattened.add(selectedFields);
    }
    return flattened;
  }

  private JsonNode convertToJson(final List<Map<String, Object>> flattened)
      throws JsonProcessingException {
    final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    final String json = ow.writeValueAsString(flattened);
    return new ObjectMapper().readTree(json);
  }

  private void writeToCsv(final JsonNode jsonTree) throws IOException {
    final JsonNode jsonNode = jsonTree.elements().next();
    final Builder csvSchemaBuilder = CsvSchema.builder();
    jsonNode.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
    final CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
    final CsvMapper csvMapper = new CsvMapper();
    csvMapper
        .writerFor(JsonNode.class)
        .with(csvSchema)
        .writeValue(new File("persons.csv"), jsonTree);
  }
}
