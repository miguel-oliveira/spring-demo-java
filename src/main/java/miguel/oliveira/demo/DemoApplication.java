package miguel.oliveira.demo;

import miguel.oliveira.demo.jpa.MyJpaAuditor;
import miguel.oliveira.demo.jpa.MyJpaDateTimeProvider;
import miguel.oliveira.demo.scope.ThreadScope;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@EnableJpaAuditing(auditorAwareRef = MyJpaAuditor.NAME, dateTimeProviderRef = MyJpaDateTimeProvider.NAME)
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  public BeanFactoryPostProcessor customBeanFactoryPostProcessor() {
    return beanFactory -> beanFactory.registerScope("thread", new ThreadScope());
  }

}
