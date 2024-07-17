package banco.banco.controller;

import banco.banco.dto.CancelRequest;
import banco.banco.dto.PurchaseRequest;
import banco.banco.exeption.ErrorDetails;
import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Transaction;
import banco.banco.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<Object> makePurchase(@RequestBody PurchaseRequest request) {
        try {
            Transaction transaction = transactionService.makePurchase(request.getCardId(), request.getPrice());

            // Construir la respuesta personalizada
            Map<String, Object> response = new HashMap<>();
            response.put("cardId", transaction.getCard().getCardId());
            response.put("balance", transaction.getCard().getBalance());

            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("id", transaction.getId());
            transactionDetails.put("amount", transaction.getAmount());
            transactionDetails.put("transactionDate", transaction.getTransactionDate());
            transactionDetails.put("status", transaction.getStatus());

            response.put("transaction", transactionDetails);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorDetails(e.getMessage(), "Card not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(new ErrorDetails(e.getMessage(), "Card cannot be used for purchase"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorDetails(e.getMessage(), "Amount exceeds card balance"));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Object> getTransaction(@PathVariable long transactionId) {
        try {
            Optional<Transaction> optionalTransaction = transactionService.getTransaction(transactionId);
            if (optionalTransaction.isPresent()) {
                Transaction transaction = optionalTransaction.get();

                // Construir la respuesta personalizada
                Map<String, Object> response = new HashMap<>();
                response.put("cardId", transaction.getCard().getCardId());
                response.put("balance", transaction.getCard().getBalance());

                Map<String, Object> transactionDetails = new HashMap<>();
                transactionDetails.put("id", transaction.getId());
                transactionDetails.put("amount", transaction.getAmount());
                transactionDetails.put("transactionDate", transaction.getTransactionDate());
                transactionDetails.put("status", transaction.getStatus());

                response.put("transaction", transactionDetails);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(new ErrorDetails("Transaction not found", "Transaction with id " + transactionId + " was not found"));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorDetails(e.getMessage(), "Transaction not found"));
        }
    }
    

    @PostMapping("/anulation")
    public ResponseEntity<Object> cancelTransaction(@RequestBody CancelRequest request) {
        try {
            Transaction transaction = transactionService.cancelTransaction(request.getCardId(), request.getTransactionId());

            // Construir la respuesta personalizada
            Map<String, Object> response = new HashMap<>();
            response.put("cardId", transaction.getCard().getCardId());
            response.put("balance", transaction.getCard().getBalance());

            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("id", transaction.getId());
            transactionDetails.put("amount", transaction.getAmount());
            transactionDetails.put("transactionDate", transaction.getTransactionDate());
            transactionDetails.put("status", transaction.getStatus());

            response.put("transaction", transactionDetails);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}
