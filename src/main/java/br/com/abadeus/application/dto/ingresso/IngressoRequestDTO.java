package br.com.abadeus.application.dto.ingresso;

import jakarta.validation.constraints.NotNull;

public record IngressoRequestDTO(
        @NotNull
        Long eventoId,

        @NotNull
        Long pedidoId
) {
}
