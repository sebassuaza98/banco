package banco.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import banco.banco.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}
