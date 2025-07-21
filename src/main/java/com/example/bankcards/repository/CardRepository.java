package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByUser(User user, Pageable pageable);

    List<Card> findAllByStatusAndExpiryDateBefore(CardStatus status, LocalDate date);
}
