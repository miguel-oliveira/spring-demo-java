package miguel.oliveira.demo.mongodb;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
@RequestMapping("/persons")
@CrossOrigin
@AllArgsConstructor
public class PersonController {

  private final PersonService personService;

  @PostMapping
  public ResponseEntity<Void> post(@RequestBody Person person) {
    final String createdId = personService.create(person);
    return ResponseEntity.created(buildURI(createdId).toUri()).build();
  }

  private UriComponents buildURI(String id) {
    return ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> put(@PathVariable String id, @RequestBody Person person) {
    final Optional<Person> updated = personService.update(id, person);
    return updated.isPresent() ?
        ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.LOCATION, buildURI(id).toUriString()).build() :
        ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<Page<Person>> get(Pageable pageable) {
    return ResponseEntity.ok(personService.get(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Person> getById(@PathVariable String id) {
    return ResponseEntity.of(personService.get(id));
  }

  @PostMapping("/export")
  public ResponseEntity<Void> export(@RequestBody ExportRequest exportRequest) {
    personService.export(exportRequest);
    return ResponseEntity.accepted().build();
  }

}
