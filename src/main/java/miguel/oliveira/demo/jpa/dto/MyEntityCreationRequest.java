package miguel.oliveira.demo.jpa.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyEntityCreationRequest {

  @NotBlank
  private String code;

  @NotBlank
  private String namespace;

  @NotBlank
  private String name;

}
