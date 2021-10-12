package miguel.oliveira.demo.record;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Aspect
@Component
@AllArgsConstructor
public class RecordingService {

  private final ProducerTemplate producer;

  @AfterReturning("@annotation(miguel.oliveira.demo.record.Record)")
  public void record(JoinPoint joinPoint) {
    Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    logger.info("Registering method call {} with args {}", joinPoint.getSignature(),
        joinPoint.getArgs());

    Event event = new Event();
    Signature signature = joinPoint.getSignature();
    event.setBeanName(extractBeanName((MethodSignature) signature));
    event.setMethodName(signature.getName());
    event.setParameterTypes(((MethodSignature) signature).getParameterTypes());
    event.setArgs(joinPoint.getArgs());
    producer.asyncRequestBody("direct:record", SerializationUtils.serialize(event));
  }

  private String extractBeanName(MethodSignature signature) {
    Method method = signature.getMethod();
    return method.getAnnotation(Record.class).beanName();
  }

}
