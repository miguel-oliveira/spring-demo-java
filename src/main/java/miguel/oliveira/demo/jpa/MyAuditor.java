package miguel.oliveira.demo.jpa;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MyAuditor implements AuditorAware<String> {

  private final MyContextHolder contextHolder;

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(contextHolder.getUsername());
  }
}
