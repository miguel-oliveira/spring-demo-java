package miguel.oliveira.demo.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MethodCallEvent {

  private JoinPoint joinPoint;

}
