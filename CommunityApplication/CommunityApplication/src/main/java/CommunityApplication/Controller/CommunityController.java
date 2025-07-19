package CommunityApplication.Controller;

import CommunityApplication.DTO.CommunityNameResponseDTO;
import CommunityApplication.Services.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    private static final Logger logger = LoggerFactory.getLogger(CommunityController.class);

    @Autowired
    private CommunityService communityService;

    @GetMapping("/names")
    public ResponseEntity<CommunityNameResponseDTO> getAllListOfCommunity() {
        logger.info("Received request to fetch all community names.");
        List<String> names = communityService.getAllListOfCommunity();
        logger.info("Fetched {} community names successfully.", names.size());
        return ResponseEntity.ok(new CommunityNameResponseDTO(names));
    }

}
