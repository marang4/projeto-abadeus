package br.com.abadeus.application.dto.evento;

import br.com.abadeus.domain.entity.Eventos;

import java.time.LocalDateTime;

public record EventoResponseDTO(Long id, String nome, String descricao, LocalDateTime dataHora) {

    public EventoResponseDTO(Eventos evento){
        this(
                evento.getId(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataHora()
        );
    }

}
