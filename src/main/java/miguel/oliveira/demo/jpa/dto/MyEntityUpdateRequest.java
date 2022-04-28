package miguel.oliveira.demo.jpa.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import miguel.oliveira.demo.record.Info;
import miguel.oliveira.demo.record.RecordAndReplayContextConverter;
import miguel.oliveira.demo.record.RecordAndReplayContextExtractable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyEntityUpdateRequest implements RecordAndReplayContextExtractable {

  @NotBlank
  private String id;

  @NotBlank
  private String name;

  @Override
  public List<Info> extractContext(RecordAndReplayContextConverter service) {
    return service.extractFrom(this);
  }

  @Override
  public void injectContext(List<Info> info, RecordAndReplayContextConverter service) {
    this.id = service.recoverFrom(info);
  }
}
