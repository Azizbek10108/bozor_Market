package uz.bozor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiAskRequest {

    @NotBlank(message = "Savol kiritilishi shart")
    private String question;
}
