package miguel.oliveira.demo.record;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

  private final ApplicationContext context;

  public void playback(byte[] recordedBytes)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    RecordedMethodCall recordedMethodCall = (RecordedMethodCall) SerializationUtils.deserialize(
        recordedBytes);
    if (recordedMethodCall != null) {
      LOGGER.debug("Replaying recorded event: {}", recordedMethodCall);
      Object bean = context.getBean(recordedMethodCall.getBeanName());
      Method method = bean.getClass()
          .getDeclaredMethod(recordedMethodCall.getMethodName(),
              recordedMethodCall.getParameterTypes());
      // method.invoke(bean, recordedMethodCall.getArgs());
    } else {
      LOGGER.warn("Null event received, doing nothing...");
    }

  }

}
