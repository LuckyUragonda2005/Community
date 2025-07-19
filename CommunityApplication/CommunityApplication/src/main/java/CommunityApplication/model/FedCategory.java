package CommunityApplication.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "fed_categories")
@Data
public class FedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
