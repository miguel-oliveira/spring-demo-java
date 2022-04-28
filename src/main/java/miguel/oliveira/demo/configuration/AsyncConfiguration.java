package miguel.oliveira.demo.configuration;

import java.util.concurrent.Executor;
import miguel.oliveira.demo.scope.MyTaskDecorator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
    executor.setTaskDecorator(new MyTaskDecorator());
    return executor;
  }
}
