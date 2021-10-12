package miguel.oliveira.demo.record;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event implements Serializable {

  private String beanName;
  private String methodName;
  private Class<?>[] parameterTypes;
  private Object[] args;

}
