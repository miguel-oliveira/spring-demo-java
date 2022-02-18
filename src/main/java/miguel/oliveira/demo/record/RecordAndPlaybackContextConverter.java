package miguel.oliveira.demo.record;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.jpa.MyEntity;
import miguel.oliveira.demo.jpa.MyRepository;
import miguel.oliveira.demo.jpa.dto.Id;
import miguel.oliveira.demo.jpa.dto.MyEntityUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecordAndPlaybackContextConverter {

  private final MyRepository repository;

  public List<Info> extractFrom(MyEntityUpdateRequest request) {
    return extract(request.getId());
  }

  public List<Info> extractFrom(Id id) {
    return extract(id.getId());
  }

  private List<Info> extract(String id) {
    final List<Info> pairs = new LinkedList<>();
    final Optional<MyEntity> opt = repository.findById(id);
    if (opt.isPresent()) {
      final MyEntity entity = opt.get();
      pairs.add(new Info(entity.getCode(), entity.getNamespace()));
    }
    return pairs;
  }

  public String recoverFrom(List<Info> info) {
    final Info i = info.get(0);
    final MyEntity myEntity = repository.findByCodeAndNamespace(i.getCode(), i.getNamespace());
    return myEntity.getId();
  }

}
