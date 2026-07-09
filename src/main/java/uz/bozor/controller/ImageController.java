package uz.bozor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.service.ImageService;

import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * Rasm yuklash
     * POST /api/images/upload
     * Content-Type: multipart/form-data
     * Response: { "url": "/uploads/uuid.jpg" }
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'BUYER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file) {

        String url = imageService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.ok("Rasm yuklandi", Map.of("url", url)));
    }

    /**
     * Rasmni o'chirish
     * DELETE /api/images?url=/uploads/uuid.jpg
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestParam("url") String imageUrl) {

        imageService.deleteImage(imageUrl);
        return ResponseEntity.ok(ApiResponse.ok("Rasm o'chirildi", null));
    }
}
