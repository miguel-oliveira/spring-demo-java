package miguel.oliveira.demo.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.mongodb.ExportRequest.Field;
import org.springframework.integration.transformer.ObjectToMapTransformer;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@AllArgsConstructor
public class ExportService {

  public <T> void export(final ExportRequest exportRequest, final List<T> objects,
      final String filename, final boolean withHeader) throws JsonProcessingException {
    if (!CollectionUtils.isEmpty(objects)) {
      final List<Map<String, T>> flattened = compute(exportRequest, objects);
      final byte[] csvBytes = convertToCsvBytes(flattened, withHeader);
      writeToFile(csvBytes, filename);
    }
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

  private <T> byte[] convertToCsvBytes(final List<Map<String, T>> objects,
      final boolean withHeader) throws JsonProcessingException {
    final Builder csvSchemaBuilder = CsvSchema.builder();
    objects.iterator().next().keySet().forEach(csvSchemaBuilder::addColumn);
    final CsvSchema csvSchema =
        withHeader ? csvSchemaBuilder.build().withHeader() : csvSchemaBuilder.build();
    final CsvMapper csvMapper = new CsvMapper();
    return csvMapper
        .writerFor(List.class)
        .with(csvSchema)
        .writeValueAsBytes(objects);
  }

  private void writeToFile(final byte[] csvBytes, final String filename) {
    final File file = new File(filename);
    final File parent = file.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IllegalStateException("Couldn't create dir: " + parent);
    }
    try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
      fileOutputStream.write(csvBytes);
    } catch (IOException e) {
      e.printStackTrace();
      file.delete();
    }
  }

}
