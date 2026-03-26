package br.com.abadeus.application.dto.ingresso;

import br.com.abadeus.domain.entity.Ingressos;

public record IngressoResponseDTO(
        Long id,
        String codIngresso,
        boolean foiUsado
) {
    public IngressoResponseDTO(Ingressos ingresso){
        this(
                ingresso.getId(),
                ingresso.getCodIngresso(),
                ingresso.isFoiUsado()
        );
    }
}
