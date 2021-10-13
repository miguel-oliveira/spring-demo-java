package miguel.oliveira.demo.record;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.SerializationUtils;

@Component
@AllArgsConstructor
public class RecordEventPublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecordEventPublisher.class);

  private final ProducerTemplate producer;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void publish(MethodCallEvent methodCall) {
    JoinPoint joinPoint = methodCall.getJoinPoint();
    RecordedMethodCall recordedMethodCall = new RecordedMethodCall();
    Signature signature = joinPoint.getSignature();
    recordedMethodCall.setBeanName(extractBeanName((MethodSignature) signature));
    recordedMethodCall.setMethodName(signature.getName());
    recordedMethodCall.setParameterTypes(((MethodSignature) signature).getParameterTypes());
    recordedMethodCall.setArgs(joinPoint.getArgs());
    LOGGER.debug("Publishing recorded event: {}", recordedMethodCall);
    producer.asyncRequestBody("direct:record", SerializationUtils.serialize(recordedMethodCall));
  }

  private String extractBeanName(MethodSignature signature) {
    Method method = signature.getMethod();
    return method.getAnnotation(Record.class).beanName();
  }

}
