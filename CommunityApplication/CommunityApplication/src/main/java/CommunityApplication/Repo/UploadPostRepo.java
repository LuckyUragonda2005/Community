package CommunityApplication.Repo;

import CommunityApplication.model.UploadPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadPostRepo extends JpaRepository<UploadPost,Long> {
    List<UploadPost> findByFedCategory_NameIgnoreCase(String categoryName);

}
