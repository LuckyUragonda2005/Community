package CommunityApplication.Services;

import CommunityApplication.Repo.FedCategoryRepo;
import CommunityApplication.model.FedCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FedCategoryService {

    @Autowired
    private FedCategoryRepo fedCategoryRepo;

    public List<String> getAllCategoryNames() {
        return fedCategoryRepo.findAllCategoryNames();
    }

}
