package miguel.oliveira.demo.jpa.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import miguel.oliveira.demo.record.Info;
import miguel.oliveira.demo.record.RecordAndPlaybackContextConverter;
import miguel.oliveira.demo.record.RecordAndPlaybackContextExtractable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyEntityUpdateRequest implements RecordAndPlaybackContextExtractable {

  @NotBlank
  private String id;

  @NotBlank
  private String name;

  @Override
  public List<Info> extractContext(RecordAndPlaybackContextConverter service) {
    return service.extractFrom(this);
  }

  @Override
  public void injectContext(List<Info> info, RecordAndPlaybackContextConverter service) {
    this.id = service.recoverFrom(info);
  }
}
