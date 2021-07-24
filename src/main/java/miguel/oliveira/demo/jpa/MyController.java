package miguel.oliveira.demo.jpa;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entities")
@CrossOrigin
@AllArgsConstructor
public class MyController {

  private final MyService service;

  @GetMapping
  public ResponseEntity<Page<MyEntity>> get(
      @ModelAttribute MyEntityQueryParams queryParams,
      Pageable pageable) {
    return ResponseEntity.ok(service.getAll(queryParams, pageable));
  }

  @PostMapping
  public ResponseEntity<MyEntity> post(@RequestBody MyEntity entity) {
    return new ResponseEntity<>(service.create(entity), HttpStatus.CREATED);
  }

}
