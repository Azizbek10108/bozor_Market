package uz.bozor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String workDir = System.getProperty("user.dir");
        String sep = File.separator;

        // uploads/ papkasi - asosiy rasmlar papkasi
        // /uploads/products/uuid.jpg → uploads/products/uuid.jpg
        String uploadsPath = "file:" + workDir + sep + "uploads" + sep;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsPath)
                .setCachePeriod(0);

        // Eski format uchun: products/uuid.jpg yoki /products/uuid.jpg
        // → uploads/products/uuid.jpg
        String productsPath = "file:" + workDir + sep + "uploads" + sep + "products" + sep;

        registry.addResourceHandler("/products/**")
                .addResourceLocations(productsPath)
                .setCachePeriod(0);

        System.out.println("✅ /uploads/** → " + uploadsPath);
        System.out.println("✅ /products/** → " + productsPath);
    }
}
