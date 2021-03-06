package miguel.oliveira.demo.replay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miguel.oliveira.demo.record.Info;
import miguel.oliveira.demo.record.RecordAndReplayContextConverter;
import miguel.oliveira.demo.record.RecordAndReplayContextExtractable;
import miguel.oliveira.demo.record.RecordedMethodCall;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
@AllArgsConstructor
@Slf4j
public class Replayer {

  private final RecordAndReplayContextConverter contextConverter;
  private final ObjectMapper objectMapper;
  private final ApplicationContext context;

  public void replay(byte[] recordedBytes)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException {

    String json = (String) SerializationUtils.deserialize(recordedBytes);
    RecordedMethodCall recordedMethodCall = objectMapper.readValue(json,
        RecordedMethodCall.class);
    Class<?>[] types = recordedMethodCall.getParameterTypes();
    Object[] args = recordedMethodCall.getArgs();
    Object[] parsedArgs = new Object[recordedMethodCall.getArgs().length];
    for (int i = 0; i < parsedArgs.length; i++) {
      parsedArgs[i] = objectMapper.convertValue(args[i], types[i]);
    }
    log.debug("Replaying recorded event: {}", recordedMethodCall);
    log.debug("Parsed args: {}", parsedArgs);

    injectContextualInfo(recordedMethodCall, parsedArgs);

    Object bean = context.getBean(recordedMethodCall.getBeanName());
    Method method = bean.getClass()
        .getDeclaredMethod(recordedMethodCall.getMethodName(), types);
    method.invoke(bean, parsedArgs);
  }

  private void injectContextualInfo(RecordedMethodCall recordedMethodCall, Object[] parsedArgs) {
    if (recordedMethodCall.isExtractInfo()) {
      final int index = recordedMethodCall.getIndex();
      final List<Info> info = recordedMethodCall.getInfo();
      ((RecordAndReplayContextExtractable) parsedArgs[index])
          .injectContext(info, contextConverter);
    }
  }

}
