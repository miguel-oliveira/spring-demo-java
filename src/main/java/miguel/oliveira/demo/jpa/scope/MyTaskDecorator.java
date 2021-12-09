package miguel.oliveira.demo.jpa.scope;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

public class MyTaskDecorator implements TaskDecorator {

  @Override
  @NonNull
  public Runnable decorate(@NonNull Runnable runnable) {
    return new ThreadScopeRunnable(runnable);
  }
}