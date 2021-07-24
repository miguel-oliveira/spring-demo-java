package miguel.oliveira.demo.cache;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@AllArgsConstructor
public class CacheController {

  private final CacheService cacheService;

  @GetMapping(path = "/cache")
  public ResponseEntity<String> cache(@RequestParam(required = false) String key) {
    final String cachedValue =
        StringUtils.hasText(key) ? cacheService.cache(key).toString() : cacheService.cache();
    return ResponseEntity.ok(cachedValue);
  }

  @GetMapping(path = "/cache-evict")
  public ResponseEntity<Void> cacheEvict(@RequestParam(required = false) String key) {
    if (StringUtils.hasText(key)) {
      cacheService.cacheEvict(key);
    } else {
      cacheService.cacheEvict();
    }
    return ResponseEntity.ok().build();
  }
}
