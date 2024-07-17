package banco.banco.controller;

import banco.banco.dto.ActivateCardRequest;
import banco.banco.dto.CardBalanceRequest;
import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Card;
import banco.banco.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCard(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.getCard(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId)));
    }

    @GetMapping("/{productId}/number")
    public ResponseEntity<Card> generateCard(@PathVariable String productId, @RequestParam String holderName) {
        Card card = cardService.generateCard(productId, holderName);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/enroll")
    public ResponseEntity<Card> activateCard(@RequestBody ActivateCardRequest request) {
        Card card = cardService.activateCard(request.getCardId());
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> blockCard(@PathVariable String cardId) {
        String message = cardService.blockCard(cardId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/balance")
    public ResponseEntity<Card> addBalance(@RequestBody CardBalanceRequest request) {
        Card card = cardService.addBalance(request.getCardId(), request.getBalance());
        return ResponseEntity.ok(card);
    }

    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Double> getBalance(@PathVariable String cardId) {
        double balance = cardService.getBalance(cardId);
        return ResponseEntity.ok(balance);
    }
}
