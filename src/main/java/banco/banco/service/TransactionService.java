package banco.banco.service;

import banco.banco.exeption.ResourceNotFoundException;
import banco.banco.model.Card;
import banco.banco.model.Transaction;
import banco.banco.repository.CardRepository;
import banco.banco.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardService cardService;

    public Transaction makePurchase(String cardId, double amount) {
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
        cardService.validateCardStatus(card);

        if (amount > card.getBalance()) {
            throw new IllegalArgumentException("Amount exceeds card balance.");
        }

        // Procesar la transacción
        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAmount(amount);
        transaction.setTransactionDate(new Date());
        transaction.setStatus("COMPLETED");
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isEmpty()) {
            throw new ResourceNotFoundException("Transaction not found with id: " + transactionId);
        }
        return transaction;
    }

    public Transaction cancelTransaction(String cardId, long transactionId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isEmpty()) {
            throw new ResourceNotFoundException("Transaction not found with id: " + transactionId);
        }
    
        Transaction transaction = optionalTransaction.get();
        Card card = cardRepository.findByCardId(cardId);
    
        if (card == null) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }

        if (!transaction.getCard().getCardId().equals(cardId)) {
            throw new IllegalStateException("Transaction does not belong to the provided card.");
        }
    
        if ("CANCELED".equals(transaction.getStatus())) {
            throw new IllegalStateException("Transaction has already been canceled.");
        }
    
        Date now = new Date();
        long diffInMillis = now.getTime() - transaction.getTransactionDate().getTime();
        long hours = diffInMillis / (1000 * 60 * 60);
    
        if (hours > 24) {
            throw new IllegalStateException("Transaction cannot be canceled after 24 hours.");
        }
        transaction.setStatus("CANCELED");
        transactionRepository.save(transaction);
    
        card.setBalance(card.getBalance() + transaction.getAmount());
        cardRepository.save(card);
    
        return transaction;
    }
    
}
