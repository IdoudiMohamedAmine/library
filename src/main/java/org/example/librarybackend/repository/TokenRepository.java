package org.example.librarybackend.repository;

import org.example.librarybackend.Entitys.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository  extends JpaRepository<Token, Integer> {
    Optional<Token> finndByToken(String token);
}
