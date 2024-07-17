package banco.banco.service;


import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Card;
import banco.banco.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card generateCard(String productId, String holderName) {
        Card card = new Card();
        card.setCardId(generateCardNumber(productId));
        card.setProductId(productId);
        card.setHolderName(holderName);
        card.setExpiryDate(generateExpiryDate());
        card.setActive(false);
        card.setBlocked(false);
        card.setBalance(0.0);

        return cardRepository.save(card);
    }

    public Optional<Card> getCard(String cardId) {
        return Optional.ofNullable(cardRepository.findByCardId(cardId));
    }

    public Card activateCard(String cardId) {
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
        card.setActive(true);
        return cardRepository.save(card);
    }

    public String blockCard(String cardId) {
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
        card.setBlocked(true);
        cardRepository.save(card);
        return "The card with ID " + cardId + " has been successfully blocked.";
    }

    public Card addBalance(String cardId, double balance) {
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }

        validateCardStatus(card);

        card.setBalance(card.getBalance() + balance);
        return cardRepository.save(card);
    }

    public void validateCardStatus(Card card) {
        if (!card.isActive()) {
            throw new IllegalStateException("Cannot add balance. Card is not active.");
        }
        if (card.isBlocked()) {
            throw new IllegalStateException("Cannot add balance. Card is blocked.");
        }
    }

    public double getBalance(String cardId) {
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
        return card.getBalance();
    }

    private String generateCardNumber(String productId) {
        StringBuilder cardNumber = new StringBuilder(productId);
        while (cardNumber.length() < 16) {
            cardNumber.append((int) (Math.random() * 10));
        }
        return cardNumber.toString();
    }

    private Date generateExpiryDate() {
        Date currentDate = new Date();
        currentDate.setYear(currentDate.getYear() + 3);
        return currentDate;
    }

}
