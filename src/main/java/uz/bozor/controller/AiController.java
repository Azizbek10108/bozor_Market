package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.AiAskRequest;
import uz.bozor.dto.response.AiRecommendationResponse;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.service.AiService;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<AiRecommendationResponse>> ask(
            @Valid @RequestBody AiAskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(aiService.ask(request.getQuestion())));
    }
}
