package CommunityApplication.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final Neo4jClient neo4jClient;

    public String findRelationshipLabel(Long id1, Long id2) {
        String query = """
            MATCH path = shortestPath((p1:Person)-[*..6]-(p2:Person))
            WHERE elementId(p1) = $id1 AND elementId(p2) = $id2
            RETURN [rel in relationships(path) |
                    {type: type(rel),
                     start: elementId(startNode(rel)),
                     end: elementId(endNode(rel))}] AS rels
        """;

        return neo4jClient.query(query)
                .bindAll(Map.of("id1", id1, "id2", id2))
                .fetch()
                .one()
                .map(result -> {
                    List<Map<String, Object>> rels = (List<Map<String, Object>>) result.get("rels");
                    return labelRelationship(rels, id1);
                })
                .orElse("No relationship path found");
    }


    public String labelRelationship(List<Map<String, Object>> rels, Long sourceId) {
        if (rels == null || rels.isEmpty()) return "No direct relationship";

        List<String> labels = new ArrayList<>();

        for (Map<String, Object> rel : rels) {
            String type = (String) rel.get("type");
            long start = (long) rel.get("start");
            long end = (long) rel.get("end");
            String direction = (start == sourceId) ? "OUTGOING" : "INCOMING";

            String label = switch (type) {
                case "PARENT_OF" -> direction.equals("OUTGOING") ? "parent" : "child";
                case "SIBLING_OF" -> "sibling";
                case "SPOUSE_OF" -> "spouse";
                default -> "related";
            };

            labels.add(label);
            sourceId = (direction.equals("OUTGOING")) ? end : start;
        }
        return inferFinalRelation(labels);
    }

    private String inferFinalRelation(List<String> steps) {
        return switch (String.join("-", steps)) {
            case "parent-parent" -> "grandparent";
            case "child-child" -> "grandchild";
            case "parent-sibling" -> "uncle/aunt";
            case "sibling-child" -> "niece/nephew";
            case "parent-child" -> "sibling";
            case "parent-child-child" -> "cousin";
            case "child-parent" -> "sibling";
            default -> String.join(" → ", steps);
        };
    }

}
