package miguel.oliveira.demo.cache;

import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {

  private static final Random RANDOM = new Random();
  private static final String CACHE_NAME = "CACHE_NAME";

  @Cacheable(CACHE_NAME)
  public String cache() {
    log.info("Cache by name");
    return UUID.randomUUID().toString();
  }

  @Cacheable(value = CACHE_NAME, key = "#key")
  public Integer cache(String key) {
    log.info("Cache by name and key -> {}", key);
    return RANDOM.nextInt();
  }

  @CacheEvict(CACHE_NAME)
  public void cacheEvict() {
    log.info("Cache evict by name.");
  }

  @CacheEvict(value = CACHE_NAME, key = "#key")
  public void cacheEvict(String key) {
    log.info("Cache evict by name and key {}", key);
  }
}
