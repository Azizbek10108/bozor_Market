package uz.bozor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.bozor.exception.BozorException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${app.upload.path:uploads/products}")
    private String uploadPath;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/jpg"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10 MB

    public String uploadImage(MultipartFile file) {
        // Tekshiruvlar
        if (file == null || file.isEmpty()) {
            throw new BozorException("Rasm fayli bo'sh");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BozorException("Faqat JPEG, PNG, WEBP formatlar qabul qilinadi");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BozorException("Rasm hajmi 10 MB dan oshmasligi kerak");
        }

        try {
            // Papka yaratish
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Noyob fayl nomi
            String extension = getExtension(file.getOriginalFilename());
            String fileName  = UUID.randomUUID().toString() + "." + extension;
            Path   filePath  = uploadDir.resolve(fileName);

            // Saqlash
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL: /uploads/products/uuid.jpg
            String url = "/uploads/products/" + fileName;
            log.info("Rasm yuklandi: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Rasm yuklashda xatolik: {}", e.getMessage());
            throw new BozorException("Rasmni saqlashda xatolik yuz berdi");
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String fileName = imageUrl.replace("/uploads/", "");
            Path   filePath = Paths.get(uploadPath, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Rasmni o'chirishda xatolik: {}", e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
