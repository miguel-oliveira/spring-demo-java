package miguel.oliveira.demo.record;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Record {

  String beanName();

  boolean extractInfo() default false;

  int extractInfoFromParamAtIndex() default -1;

}
