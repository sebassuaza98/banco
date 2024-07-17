package banco.banco;


import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Card;
import banco.banco.repository.CardRepository;
import banco.banco.repository.TransactionRepository;
import banco.banco.service.CardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CardServiceTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CardService cardService;

    private Card testCard;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testCard = new Card();
        testCard.setCardId("9876543210987654");
        testCard.setProductId("testProduct");
        testCard.setHolderName("Test");
        testCard.setExpiryDate(new Date());
        testCard.setActive(false);
        testCard.setBlocked(false);
        testCard.setBalance(0.0);
    }



    @Test
    public void testGetCard_ExistingCardId() {
        when(cardRepository.findByCardId("9876543210987654")).thenReturn(testCard);

        Optional<Card> optionalCard = cardService.getCard("9876543210987654");

        assertTrue(optionalCard.isPresent());
        assertEquals("9876543210987654", optionalCard.get().getCardId());
    }

    @Test
    public void testGetCard_NonExistingCardId() {
        when(cardRepository.findByCardId(anyString())).thenReturn(null);

        Optional<Card> optionalCard = cardService.getCard("nonExistingCardId");

        assertTrue(optionalCard.isEmpty());
    }

    @Test
    public void testActivateCard_Success() {
        when(cardRepository.findByCardId("9876543210987654")).thenReturn(testCard);
        when(cardRepository.save(testCard)).thenReturn(testCard);

        Card activatedCard = cardService.activateCard("9876543210987654");

        assertTrue(activatedCard.isActive());
    }

    @Test
    public void testActivateCard_NotFound() {
        when(cardRepository.findByCardId(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cardService.activateCard("nonExistingCardId");
        });
    }

    @Test
    public void testBlockCard_Success() {
        when(cardRepository.findByCardId("9876543210987654")).thenReturn(testCard);
        when(cardRepository.save(testCard)).thenReturn(testCard);

        String message = cardService.blockCard("9876543210987654");

        assertEquals("The card with ID 9876543210987654 has been successfully blocked.", message);
        assertTrue(testCard.isBlocked());
    }

    @Test
    public void testBlockCard_NotFound() {
        when(cardRepository.findByCardId(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cardService.blockCard("nonExistingCardId");
        });
    }

    @Test
    public void testAddBalance_CardNotFound() {
        when(cardRepository.findByCardId(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cardService.addBalance("nonExistingCardId", 100.0);
        });
    }

    @Test
    public void testGetBalance_Success() {
        when(cardRepository.findByCardId("9876543210987654")).thenReturn(testCard);

        double balance = cardService.getBalance("9876543210987654");

        assertEquals(0.0, balance); 
    }

    @Test
    public void testGetBalance_CardNotFound() {
        when(cardRepository.findByCardId(anyString())).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> {
            cardService.getBalance("nonExistingCardId");
        });
    }
}
