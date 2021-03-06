package miguel.oliveira.demo.record;

import java.util.List;
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
public class RecordedMethodCall {

  private String beanName;
  private String methodName;
  private Class<?>[] parameterTypes;
  private Object[] args;

  private boolean extractInfo;
  private Integer index;
  private List<Info> info;

}
