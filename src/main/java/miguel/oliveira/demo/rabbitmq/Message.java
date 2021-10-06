package miguel.oliveira.demo.rabbitmq;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

  private String field1;
  private String field2;
  private String field3;
  private String[] array;
  private Map<String, Object> nestedObject;
}
