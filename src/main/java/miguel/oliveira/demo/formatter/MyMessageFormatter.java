package miguel.oliveira.demo.formatter;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

@Service
public class MyMessageFormatter {

  public String format(String message, Object... parameters) {
    if (hasEmptyPlaceholders(message)) {
      return MessageFormatter.arrayFormat(message, parameters).getMessage();
    } else {
      return indexedMessageFormatter(message, parameters);
    }
  }

  private boolean hasEmptyPlaceholders(String message) {
    String emptyPlaceholders = message.replace("\\{}", "");
    return emptyPlaceholders.contains("{}");
  }

  private String indexedMessageFormatter(String message, Object... parameters) {
    final Map<String, Object> indexedParameters = new HashMap<>();
    for (int i = 0; i < parameters.length; i++) {
      indexedParameters.put(Integer.toString(i), parameters[i]);
    }
    final StringSubstitutor formatter = new StringSubstitutor(indexedParameters, "{", "}");
    formatter.setEscapeChar('\\');
    return formatter.replace(message);
  }

}
