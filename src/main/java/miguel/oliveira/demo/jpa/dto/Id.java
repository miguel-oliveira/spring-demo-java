package miguel.oliveira.demo.jpa.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import miguel.oliveira.demo.record.RecordAndReplayContextExtractable;
import miguel.oliveira.demo.record.Info;
import miguel.oliveira.demo.record.RecordAndReplayContextConverter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Id implements RecordAndReplayContextExtractable {

  private String id;

  @Override
  public List<Info> extractContext(RecordAndReplayContextConverter service) {
    return service.extractFrom(this);
  }

  @Override
  public void injectContext(List<Info> info, RecordAndReplayContextConverter service) {
    this.id = service.recoverFrom(info);
  }
}
