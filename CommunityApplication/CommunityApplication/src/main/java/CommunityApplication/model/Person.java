package CommunityApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dob;

    @Relationship(type = "PARENT_OF")
    private List<Person> children = new ArrayList<>();

    @Relationship(type = "SPOUSE_OF", direction = Relationship.Direction.OUTGOING)
    private List<Person> spouses = new ArrayList<>();

    @Relationship(type = "SIBLING_OF", direction = Relationship.Direction.OUTGOING)
    private List<Person> siblings = new ArrayList<>();

}
