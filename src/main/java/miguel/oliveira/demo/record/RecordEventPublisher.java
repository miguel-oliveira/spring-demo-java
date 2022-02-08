package miguel.oliveira.demo.record;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.SerializationUtils;

@Slf4j
@Component
@AllArgsConstructor
public class RecordEventPublisher {

  private final RecordAndPlaybackContextService recordAndPlaybackContextService;
  private final ProducerTemplate producer;
  private final ObjectMapper objectMapper;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void publish(MethodCallEvent methodCall) throws JsonProcessingException {

    JoinPoint joinPoint = methodCall.getJoinPoint();
    RecordedMethodCall recordedMethodCall = new RecordedMethodCall();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    recordedMethodCall.setBeanName(extractBeanName(method));
    recordedMethodCall.setMethodName(signature.getName());
    recordedMethodCall.setParameterTypes(signature.getParameterTypes());
    recordedMethodCall.setArgs(joinPoint.getArgs());
    extractContextualInfo(
        recordedMethodCall,
        method,
        recordedMethodCall.getParameterTypes(),
        recordedMethodCall.getArgs()
    );

    log.debug("Publishing recorded event: {}", recordedMethodCall);
    producer
        .asyncRequestBody(
            "direct:record",
            SerializationUtils.serialize(objectMapper.writeValueAsString(recordedMethodCall)));
  }

  private String extractBeanName(Method method) {
    return method.getAnnotation(Record.class).beanName();
  }

  private boolean shouldExtractInfo(Method method) {
    final Record annotation = method.getAnnotation(Record.class);
    return annotation.extractInfoFromParamAtIndex() >= 0;
  }

  private void extractContextualInfo(
      RecordedMethodCall recordedMethodCall,
      Method method,
      Class<?>[] types,
      Object[] args
  ) {
    final Record annotation = method.getAnnotation(Record.class);

    if (annotation.extractInfo()) {
      final int index = annotation.extractInfoFromParamAtIndex();
      final Class<?> type = types[index];
      final Object arg = args[index];
      final List<Info> info = recordAndPlaybackContextService.extractFrom(type, arg);
      recordedMethodCall.setExtractInfo(true);
      recordedMethodCall.setIndex(index);
      recordedMethodCall.setInfo(info);
    }
  }

}
