package miguel.oliveira.demo.jpa;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component(MyAuditor.NAME)
@AllArgsConstructor
public class MyAuditor implements AuditorAware<String> {

  public static final String NAME = "myAuditor";

  private final MyContextHolder contextHolder;

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(contextHolder.getUsername());
  }
}
