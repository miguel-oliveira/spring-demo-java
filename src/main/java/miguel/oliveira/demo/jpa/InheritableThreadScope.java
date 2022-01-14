package miguel.oliveira.demo.jpa;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.lang.NonNull;

public class InheritableThreadScope implements Scope {

  private static final Log logger = LogFactory.getLog(InheritableThreadScope.class);

  private final ThreadLocal<Map<String, Object>> threadScope =
      new NamedInheritableThreadLocal<Map<String, Object>>("InheritableThreadScope") {
        @Override
        protected Map<String, Object> initialValue() {
          return new HashMap<>();
        }
      };


  @Override
  @NonNull
  public Object get(@NonNull String name, @NonNull ObjectFactory<?> objectFactory) {
    Map<String, Object> scope = this.threadScope.get();
    // NOTE: Do NOT modify the following to use Map::computeIfAbsent. For details,
    // see https://github.com/spring-projects/spring-framework/issues/25801.
    Object scopedObject = scope.get(name);
    if (scopedObject == null) {
      scopedObject = objectFactory.getObject();
      scope.put(name, scopedObject);
    }
    return scopedObject;
  }

  @Override
  public Object remove(@NonNull String name) {
    Map<String, Object> scope = this.threadScope.get();
    return scope.remove(name);
  }

  @Override
  public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
    logger.warn("InheritableThreadScope does not support destruction callbacks. " +
        "Consider using RequestScope in a web environment.");
  }

  @Override
  public Object resolveContextualObject(@NonNull String key) {
    return null;
  }

  @Override
  public String getConversationId() {
    return Thread.currentThread().getName();
  }

}