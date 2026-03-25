package br.com.abadeus.application.dto.evento;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventoRequestDTO(
        Long id,

        @NotBlank(message = "O nome do evento é obrigatório")
        String nome,

        @NotBlank(message = "A descrição do evento é obrigatória")
        String descricao,

        @NotNull(message = "A ocupação máxima é obrigatória")
        Integer quantidadeOcupacao,

        @NotNull(message = "A data e hora do evento são obrigatórias")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm") // Padronizado para facilitar o uso
        LocalDateTime dataHora
) {}