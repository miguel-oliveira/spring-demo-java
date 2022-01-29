package miguel.oliveira.demo.jpa;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component(MyJpaAuditor.NAME)
@AllArgsConstructor
public class MyJpaAuditor implements AuditorAware<String> {

  public static final String NAME = "myAuditor";

  private final MyContextHolder contextHolder;

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(contextHolder.getUsername());
  }
}
