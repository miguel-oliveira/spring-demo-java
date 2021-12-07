package miguel.oliveira.demo;

import miguel.oliveira.demo.jpa.InheritableThreadScope;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableAsync
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  public BeanFactoryPostProcessor customBeanFactoryPostProcessor() {
    return beanFactory -> beanFactory.registerScope("thread", new InheritableThreadScope());
  }

}
