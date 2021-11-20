package miguel.oliveira.demo.formatter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

@Service
public class MyMessageFormatter {

  public String format(String message, Object... parameters) {
    if (message.contains("{}")) {
      return MessageFormatter.arrayFormat(message, parameters).getMessage();
    } else {
      return indexedMessageFormatter(message, parameters);
    }
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

  private String javaMessageFormat(String message, Object... parameters) {
    String pattern = message.replace("'", "''");
    pattern = pattern.replaceAll("(\\{)(\\D+|$)", "'$1'$2");
    return MessageFormat.format(pattern, parameters);
  }

  private String indexEmptyPlaceHolders(String original) {
    StringBuilder sb = new StringBuilder();
    Pattern p = Pattern.compile("\\{}");
    Matcher m = p.matcher(original);

    int i = 0;
    while (m.find()) {
      m.appendReplacement(sb, String.format("{%d}", i++));
    }
    m.appendTail(sb);

    return sb.toString();
  }
}
