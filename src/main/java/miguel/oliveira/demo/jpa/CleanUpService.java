package miguel.oliveira.demo.jpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CleanUpService {

  private final CleanUpRepository cleanUpRepository;

  public void cleanUp() {
    cleanUpRepository.cleanUp();
  }
}
