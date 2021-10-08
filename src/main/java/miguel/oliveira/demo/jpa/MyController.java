package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/revisions")
  public ResponseEntity<List<MyEntity>> get(@RequestParam(required = false) Instant until) {
    final List<MyEntity> result =
        until != null ? service.getLatestRevisionUntil(until) : service.getAllRevisions();
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<MyEntity> post(@RequestBody MyEntity entity) {
    return new ResponseEntity<>(service.create(entity), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MyEntity> put(
      @PathVariable String id,
      @RequestBody MyEntity entity
  ) {
    return new ResponseEntity<>(service.update(id, entity), HttpStatus.OK);
  }

  @PostMapping("/snapshot")
  public ResponseEntity<Void> snapshot(@RequestParam Instant time) {
    service.snapshot(time);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
