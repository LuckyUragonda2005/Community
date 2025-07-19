package CommunityApplication.Controller;

import CommunityApplication.Repo.PersonRepository;
import CommunityApplication.Services.RelationshipService;
import CommunityApplication.model.Person;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;
    private final RelationshipService relationshipService;

    @PostMapping("/register")
    public ResponseEntity<Person> register(@RequestBody Person person) {
        return ResponseEntity.ok(personRepository.save(person));
    }

    @PostMapping("/add-parent")
    public ResponseEntity<?> addParent(@RequestParam Long parentId, @RequestParam Long childId) {
        Person parent = personRepository.findById(parentId).orElseThrow();
        Person child = personRepository.findById(childId).orElseThrow();
        parent.getChildren().add(child);
        personRepository.save(parent);
        return ResponseEntity.ok("Parent relationship added.");
    }

    @GetMapping("/relation")
    public ResponseEntity<Map<String, String>> getRelationLabel(@RequestParam Long id1, @RequestParam Long id2) {
        String label = relationshipService.findRelationshipLabel(id1, id2);
        return ResponseEntity.ok(Map.of("relationshipLabel", label));
    }

}
