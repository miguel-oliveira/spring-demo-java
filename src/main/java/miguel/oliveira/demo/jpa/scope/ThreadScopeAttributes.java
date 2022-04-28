package miguel.oliveira.demo.jpa.scope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@NoArgsConstructor
@Slf4j
public class ThreadScopeAttributes {

  private Map<String, Object> beanMap = new HashMap<>();
  private Map<String, Runnable> destructionCallbacks = new LinkedHashMap<>();

  public ThreadScopeAttributes(ThreadScopeAttributes threadScopeAttributes) {
    this.beanMap = new HashMap<>(threadScopeAttributes.beanMap);
    this.destructionCallbacks =
        new LinkedHashMap<>(threadScopeAttributes.destructionCallbacks);
  }

  protected final Map<String, Object> getBeanMap() {
    return beanMap;
  }

  protected final void registerRequestDestructionCallback(String name, Runnable callback) {
    Assert.notNull(name, "Name must not be null");
    Assert.notNull(callback, "Callback must not be null");

    destructionCallbacks.put(name, callback);
  }

  public final void clear() {
    processDestructionCallbacks();
    beanMap.clear();
  }

  private void processDestructionCallbacks() {
    for (String name : destructionCallbacks.keySet()) {
      Runnable callback = destructionCallbacks.get(name);

      log.debug("Performing destruction callback for '" + name + "' bean" +
          " on thread '" + Thread.currentThread().getName() + "'.");

      callback.run();
    }

    destructionCallbacks.clear();
  }

}