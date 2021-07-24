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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.mongodb.ExportRequest.Field;
import org.springframework.integration.transformer.ObjectToMapTransformer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportService {

  public <T> void export(final ExportRequest exportRequest, final List<T> objects)
      throws IOException {
    final List<Map<String, T>> flattened = compute(exportRequest, objects);
    final JsonNode jsonTree = convertToJson(flattened);
    writeToCsv(jsonTree);
  }

  private <T> List<Map<String, T>> compute(
      final ExportRequest exportRequest,
      final List<T> objects) {

    final ObjectToMapTransformer transformer = new ObjectToMapTransformer();
    transformer.setShouldFlattenKeys(true);
    final List<Map<String, T>> flattened = new LinkedList<>();

    for (Object object : objects) {
      final Map<String, T> completeObjectMap =
          (Map<String, T>) transformer.transform(new GenericMessage<>(object)).getPayload();

      final Map<String, T> selectedFields = new LinkedHashMap<>();

      for (Field field : exportRequest.getFields()) {
        selectedFields.put(field.getName(), completeObjectMap.getOrDefault(field.getPath(), null));
      }

      flattened.add(selectedFields);
    }

    return flattened;
  }

  private <T> JsonNode convertToJson(final List<Map<String, T>> flattened)
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
        .writeValue(new File(String.format("%s.csv", UUID.randomUUID().toString())), jsonTree);
  }

}
