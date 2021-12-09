package miguel.oliveira.demo.jpa.scope;

import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.NonNull;

public class ThreadScope implements Scope {

  @Override
  @NonNull
  public Object get(@NonNull String name, @NonNull ObjectFactory<?> factory) {
    Map<String, Object> scope = ThreadScopeContextHolder.currentThreadScopeAttributes()
        .getBeanMap();
    // NOTE: Do NOT modify the following to use Map::computeIfAbsent. For details,
    // see https://github.com/spring-projects/spring-framework/issues/25801.
    Object scopedObject = scope.get(name);
    if (scopedObject == null) {
      scopedObject = factory.getObject();
      scope.put(name, scopedObject);
    }
    return scopedObject;
  }

  @Override
  public Object remove(@NonNull String name) {
    Map<String, Object> scope = ThreadScopeContextHolder.currentThreadScopeAttributes()
        .getBeanMap();
    return scope.remove(name);
  }

  @Override
  public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
    ThreadScopeContextHolder.currentThreadScopeAttributes()
        .registerRequestDestructionCallback(name, callback);
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