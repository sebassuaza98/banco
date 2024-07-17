package banco.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import banco.banco.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByCardId(String cardId);
}