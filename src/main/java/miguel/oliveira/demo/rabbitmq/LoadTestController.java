package miguel.oliveira.demo.rabbitmq;

import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-rabbitmq-load")
@CrossOrigin
@AllArgsConstructor
public class LoadTestController {

  private final LoadTestService loadTestService;

  @PostMapping
  public ResponseEntity<Void> load(@RequestParam int requests) {
    final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
    completableFuture.completeAsync(() -> {
      for (int i = 0; i < requests; i++) {
        loadTestService.produceRandomMessage();
      }
      return null;
    });
    return ResponseEntity.accepted().build();
  }
}
