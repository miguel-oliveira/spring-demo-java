package miguel.oliveira.demo.jpa;

import static miguel.oliveira.demo.jpa.MyController.BEAN_NAME;

import java.time.Instant;
import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.jpa.dto.Id;
import miguel.oliveira.demo.jpa.dto.MyEntityCreationRequest;
import miguel.oliveira.demo.jpa.dto.MyEntityQueryParams;
import miguel.oliveira.demo.jpa.dto.MyEntityUpdateRequest;
import miguel.oliveira.demo.jpa.scope.ThreadScopeContextHolder;
import miguel.oliveira.demo.record.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(BEAN_NAME)
@RequestMapping("/entities")
@CrossOrigin
@AllArgsConstructor
public class MyController {

  static final String BEAN_NAME = "MyController";

  private final MyService service;
  private final MyContextHolder contextHolder;
  private final CleanUpService cleanUpService;

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
  @Record(beanName = BEAN_NAME)
  @Transactional
  public ResponseEntity<MyEntity> post(
      @RequestParam(required = false) Long timestamp,
      @RequestBody MyEntityCreationRequest request
  ) {
    if (timestamp != null) {
      contextHolder.setTimestamp(timestamp);
    }
    contextHolder.setUsername(request.getName());
    service.asyncContextTest();
    final MyEntity created = service.create(convert(request));
    ThreadScopeContextHolder.currentThreadScopeAttributes().clear();
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  private MyEntity convert(MyEntityCreationRequest request) {
    final MyEntity myEntity = new MyEntity();
    myEntity.setCode(request.getCode());
    myEntity.setNamespace(request.getNamespace());
    myEntity.setName(request.getName());
    return myEntity;
  }

  @PutMapping
  @Record(beanName = BEAN_NAME, extractInfo = true, extractInfoFromParamAtIndex = 0)
  @Transactional
  public ResponseEntity<MyEntity> put(
      @RequestBody MyEntityUpdateRequest request
  ) {
    return new ResponseEntity<>(service.update(request), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @Transactional
  @Record(beanName = BEAN_NAME, extractInfo = true, extractInfoFromParamAtIndex = 0)
  public ResponseEntity<Void> delete(@PathVariable Id id) {
    service.delete(id.getId());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/snapshot")
  public ResponseEntity<Void> snapshot(@RequestParam Long time) {
    service.snapshot(time);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAll() {
    cleanUpService.cleanUp();
    return ResponseEntity.noContent().build();
  }

}
