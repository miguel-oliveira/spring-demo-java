package miguel.oliveira.demo.cache;

import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

  private static final Random RANDOM = new Random();
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
  private static final String CACHE_NAME = "CACHE_NAME";

  @Cacheable(CACHE_NAME)
  public String cache() {
    LOGGER.info("Cache by name");
    return UUID.randomUUID().toString();
  }

  @Cacheable(value = CACHE_NAME, key = "#key")
  public Integer cache(String key) {
    LOGGER.info("Cache by name and key -> {}", key);
    return RANDOM.nextInt();
  }

  @CacheEvict(CACHE_NAME)
  public void cacheEvict() {
    LOGGER.info("Cache evict by name.");
  }

  @CacheEvict(value = CACHE_NAME, key = "#key")
  public void cacheEvict(String key) {
    LOGGER.info("Cache evict by name and key {}", key);
  }
}
