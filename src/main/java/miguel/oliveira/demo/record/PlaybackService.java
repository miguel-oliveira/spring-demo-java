package miguel.oliveira.demo.record;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
@AllArgsConstructor
public class PlaybackService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlaybackService.class);

  private final RecordAndPlaybackContextConverter contextConverter;
  private final ObjectMapper objectMapper;
  private final ApplicationContext context;

  public void playback(byte[] recordedBytes)
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
    LOGGER.debug("Replaying recorded event: {}", recordedMethodCall);
    LOGGER.debug("Parsed args: {}", parsedArgs);

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
      ((RecordAndPlaybackContextExtractable) parsedArgs[index])
          .injectContext(info, contextConverter);
    }
  }

}
