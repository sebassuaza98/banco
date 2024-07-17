package banco.banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EnrollRequest {
    @NotBlank
    @Size(min = 16, max = 16)
    private String cardId;

    // Getters y setters
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
