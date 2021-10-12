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

  public void playback(byte[] eventBytes)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    Event event = (Event) SerializationUtils.deserialize(eventBytes);
    if (event != null) {
      LOGGER.info("Playback event: {}", event);
      Object bean = context.getBean(event.getBeanName());
      Method method = bean.getClass()
          .getDeclaredMethod(event.getMethodName(), event.getParameterTypes());
      // method.invoke(bean, event.getArgs());
    } else {
      LOGGER.warn("Null event received, doing nothing...");
    }

  }

}
