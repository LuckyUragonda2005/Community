package CommunityApplication.Services;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCompressor {


    public static File compressImage(MultipartFile imageFile, float quality) throws IOException {
        // Convert MultipartFile to BufferedImage
        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());

        // Create a temporary file
        File compressedImageFile = File.createTempFile("compressed_", ".jpg");

        // Compress and write to file
        Thumbnails.of(originalImage)
                .scale(1.0) // no resizing only quality compression
                .outputQuality(quality) // quality
                .toFile(compressedImageFile);

        return compressedImageFile;
    }
}
