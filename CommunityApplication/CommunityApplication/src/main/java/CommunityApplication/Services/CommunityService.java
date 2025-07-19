package CommunityApplication.Services;

import CommunityApplication.Repo.CommunityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepo communityRepo;

    public List<String> getAllListOfCommunity() {
        return communityRepo.findAllCommunityNames();
    }

}
