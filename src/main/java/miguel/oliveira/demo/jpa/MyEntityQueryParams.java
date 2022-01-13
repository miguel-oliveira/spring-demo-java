package miguel.oliveira.demo.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyEntityQueryParams {

  private String name;

  private Long createdAt;

  private Long createdAtFrom;

  private Long createdAtTo;

  private Long modifiedAtFrom;

  private Long modifiedAtTo;

}
