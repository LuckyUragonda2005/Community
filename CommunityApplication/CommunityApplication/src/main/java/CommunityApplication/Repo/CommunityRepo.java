package CommunityApplication.Repo;

import CommunityApplication.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunityRepo extends JpaRepository<Community, Long> {

    @Query("SELECT c.name FROM Community c")
    List<String> findAllCommunityNames();
}
