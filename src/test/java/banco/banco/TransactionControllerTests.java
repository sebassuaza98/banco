package banco.banco;

import banco.banco.controller.TransactionController;
import banco.banco.dto.CancelRequest;
import banco.banco.dto.PurchaseRequest;

import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Card;
import banco.banco.model.Transaction;
import banco.banco.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TransactionControllerTests {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    public void makePurchase_ValidRequest_ReturnsOk() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setCardId("9876543210987654");
        request.setPrice(50.0);

        Transaction mockTransaction = new Transaction();
        Card mockCard = new Card();
        mockCard.setCardId(request.getCardId());
        mockCard.setBalance(100.0);
        mockTransaction.setId(1L);
        mockTransaction.setCard(mockCard);
        mockTransaction.setAmount(request.getPrice());
        mockTransaction.setStatus("COMPLETED");

        when(transactionService.makePurchase(request.getCardId(), request.getPrice())).thenReturn(mockTransaction);
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cardId\":\"9876543210987654\",\"price\":50.0}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(mockTransaction.getCard().getCardId()))
                .andExpect(jsonPath("$.balance").value(mockTransaction.getCard().getBalance()))
                .andExpect(jsonPath("$.transaction.id").value(mockTransaction.getId()))
                .andExpect(jsonPath("$.transaction.amount").value(mockTransaction.getAmount()))
                .andExpect(jsonPath("$.transaction.status").value(mockTransaction.getStatus()));
    }

    @Test
    public void makePurchase_CardNotFound_ReturnsNotFound() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setCardId("9876543210987654");
        request.setPrice(50.0);

        when(transactionService.makePurchase(request.getCardId(), request.getPrice())).thenThrow(new ResourceNotFoundException("Card not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cardId\":\"9876543210987654\",\"price\":50.0}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Card not found"))
                .andExpect(jsonPath("$.details").value("Card not found"));
    }

    @Test
    public void cancelTransaction_ValidRequest_ReturnsOk() throws Exception {
        CancelRequest request = new CancelRequest();
        request.setCardId("9876543210987654");
        request.setTransactionId(1L);

        Transaction mockTransaction = new Transaction();
        Card mockCard = new Card();
        mockCard.setCardId(request.getCardId());
        mockCard.setBalance(100.0);
        mockTransaction.setId(request.getTransactionId());
        mockTransaction.setCard(mockCard);
        mockTransaction.setAmount(50.0);
        mockTransaction.setStatus("CANCELLED");

        when(transactionService.cancelTransaction(request.getCardId(), request.getTransactionId())).thenReturn(mockTransaction);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/anulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cardId\":\"9876543210987654\",\"transactionId\":1}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(mockTransaction.getCard().getCardId()))
                .andExpect(jsonPath("$.balance").value(mockTransaction.getCard().getBalance()))
                .andExpect(jsonPath("$.transaction.id").value(mockTransaction.getId()))
                .andExpect(jsonPath("$.transaction.amount").value(mockTransaction.getAmount()))
                .andExpect(jsonPath("$.transaction.status").value(mockTransaction.getStatus()));
    }

    @Test
    public void getTransaction_ValidTransactionId_ReturnsOk() throws Exception {
        long transactionId = 1L;

        Transaction mockTransaction = new Transaction();
        Card mockCard = new Card();
        mockCard.setCardId("9876543210987654");
        mockCard.setBalance(100.0);
        mockTransaction.setId(transactionId);
        mockTransaction.setCard(mockCard);
        mockTransaction.setAmount(50.0);
        mockTransaction.setStatus("COMPLETED");

        when(transactionService.getTransaction(transactionId)).thenReturn(Optional.of(mockTransaction));

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", transactionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(mockTransaction.getCard().getCardId()))
                .andExpect(jsonPath("$.balance").value(mockTransaction.getCard().getBalance()))
                .andExpect(jsonPath("$.transaction.id").value(mockTransaction.getId()))
                .andExpect(jsonPath("$.transaction.amount").value(mockTransaction.getAmount()))
                .andExpect(jsonPath("$.transaction.status").value(mockTransaction.getStatus()));
    }

    @Test
    public void getTransaction_NonExistingTransactionId_ReturnsNotFound() throws Exception {
        long transactionId = 1L;

        when(transactionService.getTransaction(transactionId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", transactionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found"))
                .andExpect(jsonPath("$.details").value("Transaction with id 1 was not found"));
    }
}
