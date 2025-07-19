package CommunityApplication.Controller;

import CommunityApplication.DTO.FedCategoryResponseDTO;
import CommunityApplication.Services.FedCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fed-categories")
public class FedCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(FedCategoryController.class);

    @Autowired
    private FedCategoryService fedCategoryService;

    @GetMapping("/feedcategories")
    public ResponseEntity<FedCategoryResponseDTO> getCategoryNames() {
        logger.info("Received request to fetch FedCategory names.");
        List<String> categoryNames = fedCategoryService.getAllCategoryNames();
        logger.info("Fetched {} FedCategory names successfully.", categoryNames.size());
        return ResponseEntity.ok(new FedCategoryResponseDTO(categoryNames));
    }

}
