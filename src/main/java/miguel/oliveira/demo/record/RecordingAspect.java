package miguel.oliveira.demo.record;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class RecordingAspect {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecordingAspect.class);

  private final ApplicationEventPublisher eventPublisher;

  @AfterReturning("@annotation(miguel.oliveira.demo.record.Record)")
  public void record(JoinPoint joinPoint) {
    LOGGER.debug("Registering method call with signature {} and args {}", joinPoint.getSignature(),
        joinPoint.getArgs());
    eventPublisher.publishEvent(new MethodCallEvent(joinPoint));
  }


}
