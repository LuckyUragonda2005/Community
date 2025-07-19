package CommunityApplication.Repo;

import CommunityApplication.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends Neo4jRepository<Person,Long> {
    @Query("MATCH path = shortestPath((p1:Person)-[*..6]-(p2:Person)) WHERE ID(p1) = $id1 AND ID(p2) = $id2 RETURN path")
    Object findRelationshipBetween(String id1, String id2);
}
