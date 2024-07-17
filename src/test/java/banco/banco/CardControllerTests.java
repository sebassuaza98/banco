package banco.banco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import banco.banco.controller.CardController;
import banco.banco.model.Card;
import banco.banco.service.CardService;

@ExtendWith(MockitoExtension.class)
public class CardControllerTests {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private Card testCard;

    @BeforeEach
    public void setUp() {
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
    public void testGetCard_Success() {
        when(cardService.getCard(anyString())).thenReturn(Optional.of(testCard));

        ResponseEntity<Card> response = cardController.getCard("9876543210987654");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test", response.getBody().getHolderName());
    }


    @Test
    public void testActivateCard() {
        when(cardService.activateCard(anyString())).thenReturn(testCard);
        testCard.setActive(true); 

        Card activatedCard = cardService.activateCard("9876543210987654");

        assertNotNull(activatedCard);
        assertTrue(activatedCard.isActive());
    }
}
