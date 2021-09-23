package miguel.oliveira.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

  @AfterReturning("@annotation(miguel.oliveira.demo.aop.RecordEvent)")
  public void registerEvent(JoinPoint joinPoint) {
    Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    logger.info("Registering method call {} with args {}", joinPoint.getSignature(),
        joinPoint.getArgs());
  }

}
