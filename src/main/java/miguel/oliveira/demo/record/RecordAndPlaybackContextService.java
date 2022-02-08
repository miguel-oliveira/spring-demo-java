package miguel.oliveira.demo.record;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import miguel.oliveira.demo.jpa.MyEntity;
import miguel.oliveira.demo.jpa.MyRepository;
import miguel.oliveira.demo.jpa.dto.MyEntityUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public class RecordAndPlaybackContextService {

  private final MyRepository repository;

  private final Map<Class<?>, Function<Object, List<Info>>> extractors;
  private final Map<Class<?>, BiFunction<List<Info>, Object, Object>> injectors;

  public RecordAndPlaybackContextService(final MyRepository repository) {
    this.repository = repository;

    this.extractors = new HashMap<>();
    this.extractors.put(MyEntityUpdateRequest.class, o -> extractFrom((MyEntityUpdateRequest) o));
    this.extractors.put(String.class, o -> extractFrom((String) o));

    this.injectors = new HashMap<>();
    this.injectors.put(MyEntityUpdateRequest.class,
        (i, o) -> injectInto(i, (MyEntityUpdateRequest) o));
    this.injectors.put(String.class, (i, o) -> injectInto(i, (String) o));
  }

  public List<Info> extractFrom(Class<?> type, Object object) {
    return extractors.get(type).apply(object);
  }

  private List<Info> extractFrom(MyEntityUpdateRequest request) {
    return extractFrom(request.getId());
  }

  private List<Info> extractFrom(String id) {
    final List<Info> pairs = new LinkedList<>();
    final Optional<MyEntity> opt = repository.findById(id);
    if (opt.isPresent()) {
      final MyEntity entity = opt.get();
      pairs.add(new Info(entity.getCode(), entity.getNamespace()));
    }
    return pairs;
  }

  public Object injectInto(List<Info> info, Class<?> type, Object object) {
    return injectors.get(type).apply(info, object);
  }

  private Object injectInto(List<Info> info, MyEntityUpdateRequest request) {
    final Info i = info.get(0);
    final MyEntity myEntity = repository.findByCodeAndNamespace(i.getCode(), i.getNamespace());
    request.setId(myEntity.getId());
    return request;
  }

  private Object injectInto(List<Info> info, String id) {
    final Info i = info.get(0);
    final MyEntity myEntity = repository.findByCodeAndNamespace(i.getCode(), i.getNamespace());
    return myEntity.getId();
  }

}
