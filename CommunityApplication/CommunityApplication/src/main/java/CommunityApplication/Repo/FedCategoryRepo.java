package CommunityApplication.Repo;

import CommunityApplication.model.FedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FedCategoryRepo extends JpaRepository<FedCategory, Long> {

    @Query("SELECT f.name FROM FedCategory f")
    List<String> findAllCategoryNames();
}
