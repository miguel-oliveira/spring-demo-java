package miguel.oliveira.demo.record;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class RecordingAspect {

  private final ApplicationEventPublisher eventPublisher;

  @AfterReturning("@annotation(miguel.oliveira.demo.record.Record)")
  public void record(JoinPoint joinPoint) {
    log.debug("Registering method call with signature {} and args {}", joinPoint.getSignature(),
        joinPoint.getArgs());
    eventPublisher.publishEvent(new MethodCallEvent(joinPoint));
  }


}
