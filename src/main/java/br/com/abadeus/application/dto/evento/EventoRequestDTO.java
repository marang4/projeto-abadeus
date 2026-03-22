package br.com.abadeus.application.dto.evento;

import java.time.LocalDateTime;

public record EventoRequestDTO(Long id, String nome, String descricao, LocalDateTime dataHora) {
}
